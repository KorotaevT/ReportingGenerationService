import React from "react";
import { MDBContainer, MDBCol, MDBRow} from "mdb-react-ui-kit";
import { Button } from "react-bootstrap";

function App() {
  return (
    <MDBContainer fluid className="p-5 my-5">
      <MDBRow>
        <MDBCol col="4" md="6">
          <img
            src="https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-login-form/draw2.webp"
            class="img-fluid"
            alt="Sample image"
          />
        </MDBCol>

        <MDBCol col="4" md="6">
          <h1
            className="text-black mb-5 d-flex justify-content-center align-items-center"
            style={{ marginTop: "30px" }}
          >
            Генерация отчётов
          </h1>
          <div style={{ marginTop: "100px", padding: '0 60px' }}>
            <Button
              className="mb-5 w-100"
              size="lg"
              onClick={() => {
                window.location.href = "/registration";
              }}
            >
              Регистрация
            </Button>
            <Button
              className="mb-5 w-100"
              size="lg"
              onClick={() => {
                window.location.href = "/login";
              }}
            >
              Логин
            </Button>
          </div>
        </MDBCol>
      </MDBRow>
    </MDBContainer>
  );
}

export default App;
