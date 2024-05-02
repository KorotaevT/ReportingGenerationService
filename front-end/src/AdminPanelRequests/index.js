import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Col, Row, Container, Table } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";
import ajax from "../Services/fetchService";

const AdminPanelRequests = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);
  const [guestUsers, setGuestUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    userRef.current = user;
    navigateRef.current = navigate;
  }, [user, navigate]);

  useEffect(() => {
    const checkTokenAndFetchData = async () => {
      const isValid = await validateToken(userRef.current.jwt);
      if (!isValid) {
        userRef.current.setJwt(null);
        navigateRef.current("/login");
      }
    };

    checkTokenAndFetchData();
  }, [userRef, navigateRef]);

  useInterval(async () => {
    const isValid = await validateToken(userRef.current.jwt);
    if (!isValid) {
      userRef.current.setJwt(null);
      navigateRef.current("/login");
    }
  }, 60000);

  useEffect(() => {
    setIsLoading(true);
    ajax(`/api/admin/getAuthRequests`, "GET", user.jwt).then((response) => {
      setGuestUsers(response.sort((a, b) => a.registrationDate.localeCompare(b.registrationDate)));
      setIsLoading(false);
    });
  }, []);

  const handleApprove = (userId) => {
    const userToApprove = guestUsers.find(
      (guestUser) => guestUser.id === userId
    );
    ajax(
      `/api/admin/approveAuthRequest`,
      "PATCH",
      user.jwt,
      userToApprove
    ).then(() => {
      const updatedGuestUsers = guestUsers.filter(
        (guestUser) => guestUser.id !== userId
      );
      setGuestUsers(updatedGuestUsers);
    });
  };

  const handleReject = (userId) => {
    ajax(`/api/admin/reject/${userId}`, "PUT", user.jwt).then(() => {
      setGuestUsers(guestUsers.filter((guestUser) => guestUser.id !== userId));
    });
  };

  return (
    <Container>
      <Row className="mt-1">
        <Col>
          <div className="d-flex justify-content-between align-items-center">
            <Button
              variant="danger"
              onClick={() => {
                navigateRef.current("/dashboard");
              }}
            >
              Back
            </Button>
            <Button
              variant="primary"
              onClick={() => {
                navigateRef.current("/admin/reports");
              }}
            >
              Reports
            </Button>
            <Button
              variant="primary"
              onClick={() => {
                navigateRef.current("/admin/users");
              }}
            >
              Users
            </Button>
            <Button
              variant="danger"
              onClick={() => {
                userRef.current.setJwt(null);
                navigateRef.current("/login");
              }}
            >
              Logout
            </Button>
          </div>
        </Col>
      </Row>

      <Row className="mt-4">
        <Col>
          <div className="h1 d-flex justify-content-center align-items-center">
            Requests
          </div>
        </Col>
      </Row>
      <div className="mt-4 report-wrapper report">
        {isLoading ? (
          <p>Загрузка...</p>
        ) : guestUsers.length > 0 ? (
          <Table striped bordered hover>
            <thead>
              <tr>
                <th className="text-center align-middle">#</th>
                <th className="text-center align-middle">Username</th>
                <th className="text-center align-middle">Registration date</th>
                <th className="text-center align-middle">Action</th>
              </tr>
            </thead>
            <tbody>
              {guestUsers.map((guestUser, index) => (
                <tr key={guestUser.id}>
                  <td className="text-center align-middle">{index + 1}</td>
                  <td className="text-center align-middle">
                    {guestUser.username}
                  </td>
                  <td className="text-center align-middle">
                    {guestUser.registrationDate}
                  </td>

                  <td className="d-flex justify-content-center">
                    <Button
                      variant="success"
                      className="mx-1"
                      onClick={() => handleApprove(guestUser.id)}
                    >
                      Approve
                    </Button>{" "}
                    <Button
                      variant="danger"
                      className="mx-1"
                      onClick={() => handleReject(guestUser.id)}
                    >
                      Reject
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <p>Нет активных запросов</p>
        )}
      </div>
    </Container>
  );
};

export default AdminPanelRequests;
