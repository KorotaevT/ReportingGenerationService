import React, { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Row, Container, Col } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";

const GuestDashboard = () => {
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

  const handleRefresh = () => {
    window.location.reload();
  };

  return (
    <Container>
      <Row className="justify-content-center align-items-center vh-100">
        <Col className="text-center mb-5">
          <div className="h1">
            Заявка отправлена. Дождитесь подтверждения регистрации.
          </div>
          <Button
            variant="success"
            className="mt-3 btn-same-size mx-2"
            onClick={handleRefresh}
          >
            Обновить страницу
          </Button>
          <Button
            variant="danger"
            className="mt-3 btn-same-size mx-2"
            onClick={() => {
              userRef.current.setJwt(null);
              navigateRef.current("/login");
            }}
          >
            Выйти
          </Button>
        </Col>
      </Row>
    </Container>
  );
};

export default GuestDashboard;
