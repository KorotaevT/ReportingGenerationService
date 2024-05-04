import React, { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Col, Row, Container } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";

const Dashboard = () => {
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
      <div className="mt-4 report-wrapper report"></div>
    </Container>
  );
};

export default Dashboard;
