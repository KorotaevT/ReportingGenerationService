import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Col, Row, Container, Table, Form } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";
import ajax from "../Services/fetchService";

const AdminPanelUsers = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);
  const [userProfiles, setUserProfiles] = useState([]);
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
    ajax(`/api/admin/getUsers`, "GET", user.jwt).then((response) => {
      const sortedUsers = response.sort((a, b) => {
        if (!a.registrationDate || !b.registrationDate) {
          return 0;
        }
        return a.registrationDate.localeCompare(b.registrationDate);
      });
      setUserProfiles(sortedUsers);
      setIsLoading(false);
    });
  }, []);

  const handleRoleChange = (event, userId) => {
    const updatedUserProfiles = userProfiles.map((userProfile) => {
      if (userProfile.user.id === userId) {
        return {
          ...userProfile,
          role: event.target.value,
        };
      }
      return userProfile;
    });
    setUserProfiles(updatedUserProfiles);
  };

  const handleSave = (userId) => {
    const userToApprove = userProfiles.find(
      (userProfile) => userProfile.user.id === userId
    );
    ajax(`/api/admin/changeUserRole`, "PATCH", user.jwt, userToApprove).then(
      () => {
        if (userToApprove.role === "GUEST") {
          const updatedUserProfiles = userProfiles.filter(
            (userProfile) => userProfile.user.id !== userId
          );
          setUserProfiles(updatedUserProfiles);
        }
      }
    );
  };
  

  const handleDelete = (userId) => {
    const userToReject = userProfiles.find(
      (userProfile) => userProfile.user.id === userId
    );
    ajax(`/api/admin/deleteUser`, "DELETE", user.jwt, userToReject).then(() => {
      const updatedUserProfiles = userProfiles.filter(
        (userProfile) => userProfile.user.id !== userId
      );
      setUserProfiles(updatedUserProfiles);
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
                navigateRef.current("/AdminPanelRequests");
              }}
            >
              Requests
            </Button>
            <Button
              variant="danger"
              onClick={() => {
                userRef.current.setJwt(null);
                navigateRef.current("/login");
              }}
            >
              Выйти
            </Button>
          </div>
        </Col>
      </Row>

      <Row className="mt-4">
        <Col>
          <div className="h1 d-flex justify-content-center align-items-center">
            Users
          </div>
        </Col>
      </Row>
      <div className="mt-4 report-wrapper report">
        {isLoading ? (
          <p>Загрузка...</p>
        ) : userProfiles.length > 0 ? (
          <Table striped bordered hover>
            <thead>
              <tr>
                <th className="text-center align-middle">#</th>
                <th className="text-center align-middle">Username</th>
                <th className="text-center align-middle">Registration date</th>
                <th className="text-center align-middle">Role</th>
                <th className="text-center align-middle">Action</th>
              </tr>
            </thead>
            <tbody>
              {userProfiles.map((userProfile, index) => (
                <tr key={userProfile.user.id}>
                  <td className="text-center align-middle">{index + 1}</td>
                  <td className="text-center align-middle">
                    {userProfile.user.username}
                  </td>
                  <td className="text-center align-middle">
                    {userProfile.user.registrationDate}
                  </td>
                  <td className="text-center align-middle">
                    <Form.Select
                      value={userProfile.role}
                      onChange={(e) => handleRoleChange(e, userProfile.user.id)}
                    >
                      <option value="GUEST">GUEST</option>
                      <option value="USER">USER</option>
                      <option value="ADMIN">ADMIN</option>
                    </Form.Select>
                  </td>
                  <td className="d-flex justify-content-center">
                    <Button
                      variant="success"
                      className="mx-1"
                      onClick={() => handleSave(userProfile.user.id)}
                    >
                      Save
                    </Button>{" "}
                    <Button
                      variant="danger"
                      className="mx-1"
                      onClick={() => handleDelete(userProfile.user.id)}
                    >
                      Delete
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

export default AdminPanelUsers;
