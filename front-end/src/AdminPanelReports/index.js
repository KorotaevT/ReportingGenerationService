import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
  Button,
  Col,
  Row,
  Container,
  Table,
  Form,
  Card,
} from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";
import ajax from "../Services/fetchService";

const AdminPanelReports = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);
  const [userProfiles, setUserProfiles] = useState([]);
  const [reports, setReports] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [addReport, setAddReport] = useState(false);

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

    // Получение отчетов
    ajax(`/api/admin/getReports`, "GET", user.jwt).then((response) => {
      setReports(response);
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
              Назад
            </Button>
            <Button
              variant="primary"
              onClick={() => {
                navigateRef.current("/AdminPanelUsers");
              }}
            >
              Пользователи
            </Button>
            <Button
              variant="primary"
              onClick={() => {
                navigateRef.current("/AdminPanelRequests");
              }}
            >
              Запросы
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

      <Row className="mt-2">
        <Col>
          <div className="h1 d-flex justify-content-center align-items-center">
            Редактирование отчётов
          </div>
        </Col>
      </Row>

      <Row className="mt-4 report-wrapper report">
        <Col>
          <Card
            style={{
              width: "100%",
              border: "2px solid #000000",
            }}
            onClick={() => {
              navigateRef.current("/CreateReport");
            }}
          >
            <Card.Body className="d-flex flex-column justify-content-center align-items-center">
              <Card.Title
                className="text-center"
                style={{ fontWeight: "bolder", fontSize: "2rem" }}
              >
                Создать отчёт
              </Card.Title>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col>
          {reports && reports.length > 0 ? (
            <div
              className="d-grid gap-5"
              style={{ gridTemplateColumns: "100%" }}
            >
              {reports.map((report) => (
                <Card
                  key={report.id}
                  style={{ width: "18rem", height: "18rem" }}
                  onClick={() => {
                    navigateRef.current(`/reports/${report.id}`);
                  }}
                >
                  <Card.Body className="d-flex flex-column justify-content-around">
                    <Card.Title>Report #{report.number}</Card.Title>
                    <div className="d-flex align-items-strat"></div>
                    <Card.Text style={{ marginTop: "1m" }}>
                      <p>
                        <b>Title:</b> {report.title}
                      </p>
                      <p>
                        <b>Author:</b> {report.author}
                      </p>
                    </Card.Text>
                  </Card.Body>
                </Card>
              ))}
            </div>
          ) : (
            <div></div>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default AdminPanelReports;
