import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Col, Row, Container, Table } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";
import ajax from "../Services/fetchService";
import { format } from "date-fns";

const AdminPanelLogs = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);
  const [logs, setLogs] = useState([]);
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
    ajax(`/api/admin/getLogs`, "GET", user.jwt).then((response) => {
      setLogs(response);
      setIsLoading(false);
    });
  }, []);

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
                navigateRef.current("/AdminPanelRequests");
              }}
            >
              Запросы
            </Button>
            <Button
              variant="primary"
              onClick={() => {
                navigateRef.current("/AdminPanelReports");
              }}
            >
              Отчёты
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
            Журнал запросов
          </div>
        </Col>
      </Row>
      <div className="mt-4 report-wrapper report">
        {isLoading ? (
          <p>Загрузка...</p>
        ) : logs.length > 0 ? (
          <Table bordered hover>
            <thead>
              <tr>
                <th className="text-center align-middle">#</th>
                <th className="text-center align-middle">ФИО</th>
                <th className="text-center align-middle">Дата получения отчёта</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log, index) => (
                <tr key={log.id}>
                  <td className="text-center align-middle">{index + 1}</td>
                  <td className="text-center align-middle">
                    {log.user ? log.user.username : ""}
                  </td>
                  <td className="text-center align-middle">
                    {format(new Date(log.requestTime), "yyyy-MM-dd HH:mm")}
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

export default AdminPanelLogs;
