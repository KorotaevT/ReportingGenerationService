import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Col, Row, Container, Card } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";
import ajax from "../Services/fetchService";

const AdminDashboard = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);
  const [reports, setReports] = useState([]);

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
    ajax(`/api/data/getReports`, "GET", user.jwt).then((response) => {
      setReports(response);
    });
  }, []);

  return (
    <Container>
      <Row className="mt-1">
        <Col>
          <div className="d-flex justify-content-between align-items-center">
            <div></div>
            <Button
              variant="danger"
              className="ml-auto"
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
            Доступные отчёты
          </div>
        </Col>
      </Row>
      <Row className="mt-4 report-wrapper report">
        <Col>
          {reports && reports.length > 0 ? (
            <div>
              {reports.map((report) => (
                <Card
                  key={report.id}
                  style={{
                    width: "100%",
                    border: "2px solid #000000",
                    marginBottom: "1rem",
                    transition:
                      "box-shadow 0.3s ease-in-out, transform 0.3s ease-in-out, background-color 0.3s ease-in-out",
                  }}
                  onMouseOver={(e) => {
                    e.currentTarget.style.boxShadow =
                      "0 4px 8px 0 rgba(0, 0, 0, 0.2)";
                    e.currentTarget.style.transform = "scale(1.02)";
                    e.currentTarget.style.backgroundColor = "#DCDCDC";
                  }}
                  onMouseOut={(e) => {
                    e.currentTarget.style.boxShadow = "none";
                    e.currentTarget.style.transform = "scale(1)";
                    e.currentTarget.style.backgroundColor = "#fff";
                  }}
                  onClick={() => {
                    navigateRef.current(`/getReport/${report.id}`);
                  }}
                  
                >
                  <Card.Body className="d-flex flex-column justify-content-around">
                    <Card.Title
                      className="text-center"
                      style={{
                        fontWeight: "bolder",
                        fontSize: "2rem",
                        textDecoration: "none",
                      }}
                    >
                      {report.name}
                    </Card.Title>
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

export default AdminDashboard;
