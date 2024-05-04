import React, { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "../UserProvider";
import ajax from "../Services/fetchService";
import { Button, Col, Row, Container, Form } from "react-bootstrap";

const CreateReport = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);

  const [reportName, setReportName] = useState("");
  const [dbUrl, setDbUrl] = useState("");
  const [dbLogin, setDbLogin] = useState("");
  const [dbPassword, setDbPassword] = useState("");
  const [availableFields, setAvailableFields] = useState("");
  const [sqlQuery, setSqlQuery] = useState("");

  const handleSubmit = (event) => {
    event.preventDefault();

    const reportData = {
      reportName,
      dbUrl,
      dbLogin,
      dbPassword,
      availableFields: availableFields.split(","),
      sqlQuery,
    };

    ajax("/api/admin/createReport", "PUT", user.jwt, reportData).then(() => {
      setReportName("");
      setDbUrl("");
      setDbLogin("");
      setDbPassword("");
      setAvailableFields("");
      setSqlQuery("");
    });
  };

  return (
    <Container className="create-report-container">
      <Row className="mt-1">
        <Col>
          <div className="d-flex justify-content-between align-items-center">
            <Button
              variant="danger"
              onClick={() => {
                navigateRef.current("/AdminPanelReports");
              }}
            >
              Назад
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
            Создание отчёта
          </div>
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <Form onSubmit={handleSubmit} className="create-report-form">
            <Form.Group controlId="reportName">
              <Form.Label className="create-report-label">
                Название отчета:
              </Form.Label>
              <Form.Control
                type="text"
                value={reportName}
                onChange={(e) => setReportName(e.target.value)}
                required
                className="create-report-input"
                placeholder="Report"
              />
            </Form.Group>
            <Form.Group controlId="dbUrl">
              <Form.Label className="create-report-label">
                Ссылка на БД:
              </Form.Label>
              <Form.Control
                type="text"
                value={dbUrl}
                onChange={(e) => setDbUrl(e.target.value)}
                required
                className="create-report-input"
                placeholder="jdbc:postgresql://localhost:5432/exampleReportDb"
              />
            </Form.Group>
            <Form.Group controlId="dbLogin">
              <Form.Label className="create-report-label">
                Логин от БД:
              </Form.Label>
              <Form.Control
                type="text"
                value={dbLogin}
                onChange={(e) => setDbLogin(e.target.value)}
                required
                className="create-report-input"
                placeholder="example"
              />
            </Form.Group>
            <Form.Group controlId="dbPassword">
              <Form.Label className="create-report-label">
                Пароль от БД:
              </Form.Label>
              <Form.Control
                type="password"
                value={dbPassword}
                onChange={(e) => setDbPassword(e.target.value)}
                required
                className="create-report-input"
                placeholder="example"
              />
            </Form.Group>
            <Form.Group controlId="availableFields">
              <Form.Label className="create-report-label">
                Доступные поля в отчете:
              </Form.Label>
              <Form.Control
                type="text"
                value={availableFields}
                onChange={(e) => setAvailableFields(e.target.value)}
                className="create-report-input"
                placeholder="name, surname, role"
              />
            </Form.Group>
            <Form.Group controlId="sqlQuery">
              <Form.Label className="create-report-label">
                SQL-запрос для отчета:
              </Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={sqlQuery}
                onChange={(e) => setSqlQuery(e.target.value)}
                className="create-report-input"
                placeholder="SELECT * FROM example_table;"
              />
            </Form.Group>
            <Button
              variant="primary"
              type="submit"
              style={{
                width: "20%",
                fontSize: "18px",
                margin: "auto",
                display: "block",
              }}
            >
              Создать отчет
            </Button>
          </Form>
        </Col>
      </Row>
    </Container>
  );
};

export default CreateReport;
