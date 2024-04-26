import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ajax from "../Services/fetchService";
import { Button, Col, Row, Table } from "react-bootstrap";
import { useUser } from "../UserProvider";

const Dashboard = () => {
  const user = useUser();
  const [tableData, setTableData] = useState(null);
  const navigate = useNavigate();
  const userRef = useRef(user);
  const navigateRef = useRef(navigate);

  useEffect(() => {
    userRef.current = user;
    navigateRef.current = navigate;
  }, [user, navigate]);

  useEffect(() => {
    ajax(`/api/assignments`, "GET", userRef.current.jwt).then(
      (assignmentsData) => {
        setTableData(assignmentsData);
      }
    );
    if (!userRef.current.jwt) {
      userRef.current.jwt = "";
      navigateRef.current("/login");
    }
  }, [userRef, navigateRef]);

  function fetchTableData() {
    ajax(`http://localhost:8080/tables`, "GET", userRef.current.jwt).then(
      (response) => {
        setTableData(response);
      }
    );
  }

  return (
    <div style={{ margin: "2em" }}>
      <Row>
        <Col>
          <div
            className="d-flex justify-content-end"
            style={{ cursor: "pointer" }}
            onClick={() => {
              userRef.current.setJwt(null);
              window.location.href = "/login";
            }}
          >
            Logout
          </div>
        </Col>
      </Row>
      <div className="mb-5 d-flex justify-content-center align-items-center">
        <Button size="lg" onClick={() => fetchTableData()}>
          Fetch Table Data
        </Button>
      </div>
      {tableData && (
        <div>
          {tableData.map((table, index) => (
            <div key={index}>
              <h3>{table.tableName}</h3>
              <Table striped bordered hover>
                <thead>
                  <tr>
                    {Object.keys(table.data).map((key) => (
                      <th key={key}>{key}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    {Object.values(table.data).map((valueArray, index) => (
                      <td key={index}>
                        {valueArray.map((value, index) => (
                          <div key={index}>{value}</div>
                        ))}
                      </td>
                    ))}
                  </tr>
                </tbody>
              </Table>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
