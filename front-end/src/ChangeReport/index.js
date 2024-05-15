import React, { useRef, useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useUser } from "../UserProvider";
import ajax from "../Services/fetchService";
import { Button, Col, Row, Container, Form } from "react-bootstrap";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";

const ChangeReport = () => {
  const user = useUser();
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);
  const { id } = useParams();

  const [name, setName] = useState("");
  const [url, setUrl] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [fields, setFields] = useState("");
  const [query, setQuery] = useState("");
  const [reportId, setReportId] = useState("");

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
    ajax(`/api/admin/getReportById/${id}`, "GET", user.jwt).then((response) => {
      setReportId(response.id);
      setName(response.name);
      setUrl(response.url);
      setUsername(response.username);
      setPassword(response.password);
      setFields(response.fields);
      setQuery(response.query);
    });
  }, [id, user.jwt]);
  
  const handleSave = () => {
    const reportData = {
      id: reportId,
      name,
      url,
      username,
      password,
      fields,
      query,
    };
  
    ajax(`/api/admin/changeReport`, "PATCH", user.jwt, reportData).then(() => {
      navigateRef.current("/AdminPanelReports");
    });
  };
  

  const handleDelete = () => {
    ajax(`/api/admin/deleteReport/${id}`, "DELETE", user.jwt).then(() => {
      navigateRef.current("/AdminPanelReports");
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
            Редактирование отчёта
          </div>
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <Form className="create-report-form">
            <Form.Group controlId="reportName">
              <Form.Label className="create-report-label">
                Название отчета:
              </Form.Label>
              <Form.Control
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
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
                value={url}
                onChange={(e) => setUrl(e.target.value)}
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
                value={username}
                onChange={(e) => setUsername(e.target.value)}
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
                value={password}
                onChange={(e) => setPassword(e.target.value)}
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
                value={fields}
                onChange={(e) => setFields(e.target.value)}
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
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                className="create-report-input"
                placeholder="SELECT * FROM example_table;"
              />
            </Form.Group>
            <div className="d-flex justify-content-center mt-3">
              <Button
                variant="primary"
                onClick={handleSave}
                style={{
                  width: "20%",
                  fontSize: "18px",
                  marginRight: "10px",
                }}
              >
                Сохранить изменения
              </Button>
              <Button
                variant="danger"
                onClick={() => {
                  handleDelete();
                }}
                style={{
                  width: "20%",
                  fontSize: "18px",
                }}
              >
                Удалить отчет
              </Button>
            </div>
          </Form>
        </Col>
      </Row>
    </Container>
  );
};

export default ChangeReport;
