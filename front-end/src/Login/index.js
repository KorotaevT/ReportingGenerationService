import React, {useState } from "react";
import { Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useUser } from "../UserProvider";
import {
  MDBContainer,
  MDBInput,
  MDBCol,
  MDBRow,
  MDBCard,
  MDBCardBody,
} from "mdb-react-ui-kit";

const Login = () => {
  const user = useUser();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  function sendLoginRequest() {
    const reqBody = {
      username: username,
      password: password,
    };

    fetch(`/api/auth/authentication`, {
      headers: {
        "Content-Type": "application/json",
      },
      method: "POST",
      body: JSON.stringify(reqBody),
    })
      .then((response) => {
        if (response.status === 200)
          return Promise.all([response.json(), response.headers]);
        else return Promise.reject("Invalid login attempt");
      })
      .then(([body, headers]) => {
        user.setJwt(headers.get("authorization"));
        window.location.href = "/dashboard";
      })
      .catch((message) => {
        alert(message);
      });
  }

  return (
    <MDBContainer
      fluid
      style={{
        backgroundColor: "#508bfc",
        height: "100vh",
        overflow: "hidden",
      }}
    >
      <MDBRow className="d-flex justify-content-center align-items-center h-100">


        <MDBCol col="12">
          <MDBCard
            className="bg-white my-5 mx-auto"
            style={{ borderRadius: "1rem", maxWidth: "500px" }}
          >
            <MDBCardBody className="p-5 w-100 d-flex flex-column">
              <h2 className="fw-bold mb-2 text-center">Sign in</h2>
              <p className="text-white-50 mb-3">
                Please enter your login and password!
              </p>

              <MDBInput
                wrapperClass="mb-4 w-100"
                type="text"
                size="lg"
                value={username}
                placeholder="Enter your name"
                onChange={(event) => setUsername(event.target.value)}
              />
              <MDBInput
                wrapperClass="mb-4 w-100"
                type="password"
                size="lg"
                value={password}
                placeholder="Enter your password"
                onChange={(event) => setPassword(event.target.value)}
              />

              <Button
                size="lg"
                id="submit"
                type="button"
                onClick={() => sendLoginRequest()}
              >
                Login
              </Button>

              <p className="text-center" style={{ marginTop: "20px" }}>
                Don't have an account?{" "}
                <a href=" " onClick={() => navigate("/registration")}>
                  Register
                </a>
              </p>

              <p className="text-center" style={{ marginTop: "-10px", marginBottom: "-35px"}}>
              Go back to the{" "}
                <a href=" " onClick={() => navigate("/")}>
                Homepage
                </a>
              </p>

            </MDBCardBody>
          </MDBCard>
        </MDBCol>
      </MDBRow>
    </MDBContainer>
  );
};

export default Login;
