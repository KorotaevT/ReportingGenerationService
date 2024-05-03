import React, { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Col, Row, Container } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";

const AdminDashboard = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);

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

  return (
    <Container>
      <Row className="mt-1">
        <Col>
          <div className="d-flex justify-content-between align-items-center">
            <Button
              variant="primary"
              onClick={() => {
                navigateRef.current("/adminPanelRequests");
              }}
            >
              Admin Panel
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
            Reporting Dashboard
          </div>
        </Col>
      </Row>
      <div className="mt-4 report-wrapper report">
        <div className="report-wrapper-title h3 px-2">
          Available reports
        </div>
      </div>
    </Container>
  );
};

export default AdminDashboard;
