import React, { useEffect, useState, useRef, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import ajax from "../Services/fetchService";
import { Button, Col, Row, Table } from "react-bootstrap";
import { useUser } from "../UserProvider";
import { useInterval } from "../util/useInterval";
import validateToken from "../util/tokenValidator";

const TABLE_HEIGHT = 25; // Примерная высота таблицы в cm
const PAGE_HEIGHT = 29.7 - 2 * 1; // Высота листа А4 без отступов в cm

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

  function fetchTableData() {
    ajax(`http://localhost:8080/api/data/all`, "GET", userRef.current.jwt).then(
      (response) => {
        setTableData(response);
      }
    );
  }

  const tableGroups = useMemo(() => {
    if (!tableData) return [];

    const groups = [];
    let currentGroup = [];
    let currentHeight = 0;

    tableData.forEach((table) => {
      currentHeight += TABLE_HEIGHT;

      if (currentHeight > PAGE_HEIGHT) {
        groups.push(currentGroup);
        currentGroup = [table];
        currentHeight = TABLE_HEIGHT;
      } else {
        currentGroup.push(table);
      }
    });

    if (currentGroup.length > 0) {
      groups.push(currentGroup);
    }

    return groups;
  }, [tableData]);

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
      {tableGroups.map((group, index) => (
        <div key={index} className="paper-container">
          <div className="page-number">Page {index + 1}</div>
          {group.map((table) => (
            <div key={table.tableName}>
              <h2>{table.dbName}</h2>
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
                          <div key={index}>
                            {value === "" ? <>&nbsp;</> : value}
                          </div>
                        ))}
                      </td>
                    ))}
                  </tr>
                </tbody>
              </Table>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
};

export default Dashboard;
