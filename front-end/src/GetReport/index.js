import React, { useRef, useState, useEffect, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useUser } from "../UserProvider";
import ajax from "../Services/fetchService";
import { Button, Col, Row, Container, Form, Table } from "react-bootstrap";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";
import { FaArrowUp, FaArrowDown } from "react-icons/fa";
import "../App.css";

const GetReport = () => {
  const user = useUser();
  const userRef = useRef(user);
  const navigate = useNavigate();
  const navigateRef = useRef(navigate);
  const { id } = useParams();
  const [fields, setFields] = useState([]);
  const [reportData, setReportData] = useState(null);

  const [report, setReport] = useState({
    id: null,
    name: "",
    fields: {
      variable: [],
      fixed: [],
    },
  });

  const [dynamicFields, setDynamicFields] = useState({
    departureAirport: "",
    departureTime: "",
  });

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
          "Предоставитель отчета",
          "Дата",
          "Количество записей",
          "Названия полей",
        ])
        .map((field) => ({ fieldName: field, isSelected: true }));

      setReport({
        id: response.id,
        name: response.name,
        fields: {
          variable: reportFields.filter(
            (field) =>
              ![
                "Название отчета",
                "Предоставитель отчета",
                "Дата",
                "Количество записей",
                "Названия полей",
              ].includes(field.fieldName)
          ),
          fixed: reportFields.filter((field) =>
            [
              "Название отчета",
              "Предоставитель отчета",
              "Дата",
              "Количество записей",
              "Названия полей",
            ].includes(field.fieldName)
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
        variable: report.fields.variable
          .filter(
            (field) =>
              fields.find((f) => f.fieldName === field.fieldName)?.isSelected
          )
          .map((field) => ({
            fieldName: field.fieldName,
          })),
        fixed: report.fields.fixed
          .filter(
            (field) =>
              fields.find((f) => f.fieldName === field.fieldName)?.isSelected
          )
          .map((field) => ({
            fieldName: field.fieldName,
          })),
      },
      dynamicFields,
    };

    ajax(`/api/data/getReportDataById`, "POST", user.jwt, reportData).then(
      (response) => {
        const sortedFixedFields = sortFixedFields(fields, response);
        setReportData({ ...response, sortedFixedFields });
      }
    );
  };

  const handleDownload = () => {
    const reportData = {
      id: report.id,
      fields: {
        variable: report.fields.variable
          .filter(
            (field) =>
              fields.find((f) => f.fieldName === field.fieldName)?.isSelected
          )
          .map((field) => ({
            fieldName: field.fieldName,
          })),
        fixed: report.fields.fixed
          .filter(
            (field) =>
              fields.find((f) => f.fieldName === field.fieldName)?.isSelected
          )
          .map((field) => ({
            fieldName: field.fieldName,
          })),
      },
      dynamicFields,
    };

    ajax(`/api/data/downloadExcel`, "POST", user.jwt, reportData)
      .then((blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "report.xlsx");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      })
      .catch((error) => {
        console.error("Error downloading Excel file:", error);
      });
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

  const moveField = (index, direction, isVariable) => {
    const targetIndex = index + direction;
    if (isVariable) {
      if (targetIndex >= 0 && targetIndex < report.fields.variable.length) {
        const newFields = [...report.fields.variable];
        const [movedField] = newFields.splice(index, 1);
        newFields.splice(targetIndex, 0, movedField);
        setReport((prevReport) => ({
          ...prevReport,
          fields: {
            ...prevReport.fields,
            variable: newFields,
          },
        }));
      }
    } else {
      if (targetIndex >= 0 && targetIndex < report.fields.fixed.length) {
        const newFields = [...report.fields.fixed];
        const [movedField] = newFields.splice(index, 1);
        newFields.splice(targetIndex, 0, movedField);
        setReport((prevReport) => ({
          ...prevReport,
          fields: {
            ...prevReport.fields,
            fixed: newFields,
          },
        }));
      }
    }
  };

  const sortFixedFields = (fields, reportData) => {
    const fixedFieldOrder = report.fields.fixed.map((field) => field.fieldName);
    const sortedFixedFields = fixedFieldOrder
      .map((fieldName) => {
        switch (fieldName) {
          case "Название отчета":
            return {
              key: "reportName",
              label: "Название отчета",
              value: reportData.reportName,
            };
          case "Предоставитель отчета":
            return {
              key: "reportProvider",
              label: "Предоставитель отчета",
              value: reportData.reportProvider,
            };
          case "Дата":
            return {
              key: "reportDate",
              label: "Дата",
              value: new Date(reportData.reportDate).toLocaleDateString(),
            };
          case "Количество записей":
            return {
              key: "recordCount",
              label: "Количество записей",
              value: reportData.recordCount,
            };
          case "Названия полей":
            return {
              key: "fieldNames",
              label: "Названия полей",
              value: Object.keys(reportData.data[0]),
            };
          default:
            return null;
        }
      })
      .filter(
        (field) =>
          field && fields.find((f) => f.fieldName === field.label)?.isSelected
      );

    return sortedFixedFields;
  };

  const variableFields = useMemo(() => {
    return (
      report?.fields?.variable.map((field) => ({
        fieldName: field.fieldName,
      })) || []
    );
  }, [report]);

  const fixedFields = useMemo(() => {
    return (
      report?.fields?.fixed.map((field) => ({
        fieldName: field.fieldName,
      })) || []
    );
  }, [report]);

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
            {report.name}
          </div>
        </Col>
      </Row>
      <Row className="mt-3 d-flex flex-column-reverse flex-md-row">
        <Col md={6} className="d-flex flex-column align-items-center">
          <div className="create-report-label">Доступные поля в отчете:</div>
          <Table bordered hover size="sm">
            <thead>
              <tr>
                <th className="text-center">#</th>
                <th className="text-center">Поле</th>
                <th className="text-center">Выбрано</th>
                <th className="text-center">Перемещение</th>
              </tr>
            </thead>
            <tbody>
              {Array.from({ length: variableFields.length }, (_, index) => {
                const field = variableFields[index];
                return (
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
                        onChange={() => handleFieldChange(field.fieldName)}
                      />
                    </td>
                    <td className="text-center">
                      <div className="d-flex justify-content-center">
                        <Button
                          variant="link"
                          onClick={() => moveField(index, -1, true)}
                          disabled={index === 0}
                        >
                          <FaArrowUp />
                        </Button>
                        <Button
                          variant="link"
                          onClick={() => moveField(index, 1, true)}
                          disabled={index === variableFields.length - 1}
                        >
                          <FaArrowDown />
                        </Button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </Col>

        <Col md={6} className="d-flex flex-column align-items-center">
          <div className="create-report-label">Отображать в отчете:</div>
          <Table bordered hover size="sm">
            <thead>
              <tr>
                <th className="text-center">#</th>
                <th className="text-center">Поле</th>
                <th className="text-center">Выбрано</th>
                <th className="text-center">Перемещение</th>
              </tr>
            </thead>
            <tbody>
              {Array.from({ length: fixedFields.length }, (_, index) => {
                const field = fixedFields[index];
                return (
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
                        onChange={() => handleFieldChange(field.fieldName)}
                      />
                    </td>
                    <td className="text-center">
                      <div className="d-flex justify-content-center">
                        <Button
                          variant="link"
                          onClick={() => moveField(index, -1, false)}
                          disabled={index === 0}
                        >
                          <FaArrowUp />
                        </Button>
                        <Button
                          variant="link"
                          onClick={() => moveField(index, 1, false)}
                          disabled={index === fixedFields.length - 1}
                        >
                          <FaArrowDown />
                        </Button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </Col>
      </Row>
      <Row className="mt-3">
  <Col className="d-flex flex-column align-items-center">
    <div className="create-report-label">Динамические поля:</div>
    <Table bordered hover size="sm">
      <thead>
        <tr>
          <th className="text-center">#</th>
          <th className="text-center field-column">Поле</th>
          <th className="text-center value-column">Значение</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td className="text-center">1</td>
          <td className="text-left field-column">Аэропорт вылета</td>
          <td className="text-center value-column">
            <Form.Control
              type="text"
              value={dynamicFields.departureAirport}
              onChange={(e) =>
                setDynamicFields((prev) => ({
                  ...prev,
                  departureAirport: e.target.value,
                }))
              }
            />
          </td>
        </tr>
        <tr>
          <td className="text-center">2</td>
          <td className="text-left field-column">Время вылета</td>
          <td className="text-center value-column">
            <Form.Control
              type="text"
              value={dynamicFields.departureTime}
              onChange={(e) =>
                setDynamicFields((prev) => ({
                  ...prev,
                  departureTime: e.target.value,
                }))
              }
            />
          </td>
        </tr>
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
          <Button
            variant="primary"
            type="button"
            onClick={handleDownload}
            style={{
              width: "20%",
              fontSize: "18px",
              margin: "auto",
              display: "block",
              marginLeft: "10px",
            }}
          >
            Скачать отчёт
          </Button>
        </div>
      </Row>
      {reportData && (
        <Row className="mt-3">
          <Col className="table-container">
            <Table bordered hover size="sm" className="fixed-header-table">
              <tbody>
                {reportData?.sortedFixedFields?.map((field, index) => {
                  if (field.key === "fieldNames") {
                    return (
                      <tr key={index}>
                        {field.value.map((fieldName, fieldIndex) => (
                          <td key={fieldIndex} className="text-center">
                            {fieldName}
                          </td>
                        ))}
                      </tr>
                    );
                  } else {
                    return (
                      <tr key={index}>
                        <td>{field.label}:</td>
                        <td>{field.value}</td>
                      </tr>
                    );
                  }
                })}
                {reportData?.data.map((row, rowIndex) => (
                  <tr key={rowIndex}>
                    {Object.values(row).map((value, colIndex) => (
                      <td key={colIndex} className="text-center">
                        {value}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </Table>
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default GetReport;
