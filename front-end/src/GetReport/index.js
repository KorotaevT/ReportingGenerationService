import React, { useRef, useState, useEffect, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useUser } from "../UserProvider";
import ajax from "../Services/fetchService";
import { Button, Col, Row, Container, Form, Table } from "react-bootstrap";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";

const GetReport = () => {
  const user = useUser();
  const userRef = useRef(user);
  const navigate = useNavigate();
  const navigateRef = useRef(navigate);
  const { id } = useParams();

  const [report, setReport] = useState({
    id: null,
    name: "",
    fields: {
      variable: [],
      fixed: [],
    },
  });

  const [fields, setFields] = useState([]);

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
    ajax(`/api/data/getReportById/${id}`, "GET", user.jwt).then((response) => {
      const reportFields = response.fields
        .split(",")
        .map((field) => field.trim())
        .concat([
          "Название отчета",
          "Названия полей",
          "Предоставитель отчета",
        ])
        .map((field) => ({ fieldName: field, isSelected: true }));
  
      setReport({
        id: response.id,
        name: response.name,
        fields: {
          variable: reportFields.filter(
            (field) => !["Название отчета", "Названия полей", "Предоставитель отчета"].includes(field.fieldName)
          ),
          fixed: reportFields.filter(
            (field) => ["Название отчета", "Названия полей", "Предоставитель отчета"].includes(field.fieldName)
          ),
        },
      });
      setFields(reportFields);
    });
  }, [id, user.jwt]);
  

  const handleGet = () => {
    const reportData = {
      id: report.id,
      fields: {
        variable: fields
          .filter(
            (field) =>
              field.isSelected &&
              ![
                "Название отчета",
                "Названия полей",
                "Предоставитель отчета",
              ].includes(field.fieldName)
          )
          .map((field) => ({
            fieldName: field.fieldName,
          })),
        fixed: fields
          .filter(
            (field) =>
              field.isSelected &&
              [
                "Название отчета",
                "Названия полей",
                "Предоставитель отчета",
              ].includes(field.fieldName)
          )
          .map((field) => ({
            fieldName: field.fieldName,
          })),
      },
    };

    ajax(`/api/data/getReportDataById`, "POST", user.jwt, reportData).then(
      () => {}
    );
  };

  const handleFieldChange = (fieldName) => {
    setFields(
      fields.map((field) =>
        field.fieldName === fieldName
          ? { ...field, isSelected: !field.isSelected }
          : field
      )
    );
  };
  
  const variableFields = useMemo(() => {
    return report?.fields?.variable.map((field) => ({
      fieldName: field.fieldName,
    })) || [];
  }, [report]);
  
  const fixedFields = useMemo(() => {
    return report?.fields?.fixed.map((field) => ({
      fieldName: field.fieldName,
    })) || [];
  }, [report]);
  

  const maxRows = useMemo(() => {
    return Math.max(variableFields.length, fixedFields.length);
  }, [variableFields, fixedFields]);

  if (!report) {
    return <div>Загрузка...</div>;
  }

  return (
    <Container className="create-report-container">
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
              variant="danger"
              onClick={() => {
                user.setJwt(null);
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
            Отчёт: {report.name}
          </div>
        </Col>
      </Row>
      <Row className="mt-3 d-flex flex-column-reverse flex-md-row">
        <Col md={6} className="d-flex flex-column align-items-center">
          <div className="create-report-label">Доступные поля в отчете:</div>
          <Table striped bordered hover size="sm">
            <thead>
              <tr>
                <th className="text-center">#</th>
                <th className="text-center">Поле</th>
                <th className="text-center">Выбрано</th>
              </tr>
            </thead>
            <tbody>
              {variableFields.map((field, index) => (
                <tr key={index}>
                  <td className="text-center">{index + 1}</td>
                  <td className="text-left">{field.fieldName}</td>
                  <td className="text-center">
                    <Form.Check
                      type="checkbox"
                      checked={
                        fields.find((f) => f.fieldName === field.fieldName)
                          ?.isSelected || false
                      }
                      onChange={(e) =>
                        handleFieldChange(field.fieldName, e.target.checked)
                      }
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Col>

        <Col md={6} className="d-flex flex-column align-items-center">
          <div className="create-report-label">Отображать в отчете:</div>
          <Table striped bordered hover size="sm">
            <thead>
              <tr>
                <th className="text-center">#</th>
                <th className="text-center">Поле</th>
                <th className="text-center">Выбрано</th>
              </tr>
            </thead>
            <tbody>
              {Array.from({ length: maxRows }, (_, index) => {
                const field = fixedFields[index];
                return (
                  <tr key={index}>
                    <td className="text-center">
                      {field ? index + 1 : "\u00A0"}
                    </td>
                    <td className="text-left">
                      {field ? field.fieldName : ""}
                    </td>
                    <td className="text-center">
                      {field && (
                        <Form.Check
                          type="checkbox"
                          checked={
                            fields.find((f) => f.fieldName === field.fieldName)
                              ?.isSelected || false
                          }
                          onChange={(e) =>
                            handleFieldChange(field.fieldName, e.target.checked)
                          }
                        />
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </Col>
      </Row>
      <Row className="mt-3 justify-content-center">
        <div className="d-flex justify-content-center mt-3">
          <Button
            variant="primary"
            type="button"
            onClick={handleGet}
            style={{
              width: "20%",
              fontSize: "18px",
              margin: "auto",
              display: "block",
            }}
          >
            Сформировать отчёт
          </Button>
        </div>
      </Row>
    </Container>
  );
};

export default GetReport;
