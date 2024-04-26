import { useEffect, useState } from "react";
import "./App.css";
import { Route, Routes } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import Dashboard from "./Dashboard";
import CodeReviwerDashboard from "./CodeReviewerDashboard";
import Homepage from "./Homepage";
import Login from "./Login";
import PrivateRoute from "./PrivateRoute";
import AssignmentView from "./AssignmentView";
import "bootstrap/dist/css/bootstrap.min.css";
import CodeReviewerAssignmentView from "./CodeReviewerAssignmentView";
import { useUser } from "./UserProvider";
import Register from "./Register";

function App() {
  const [roles, setRoles] = useState([]);
  const user = useUser();

  useEffect(() => {
    setRoles(getRolesFromJwt());
}, [user.jwt]);


  function getRolesFromJwt() {
    if (user.jwt) {
      const decodeJwt = jwtDecode(user.jwt);
      return decodeJwt.authorities;
    }
  }

  return (
    <Routes>
      <Route
        path="/dashboard"
        element={
          roles && roles.find((role) => role === "REVIEWER") ? (
            <PrivateRoute>
              <CodeReviwerDashboard />
            </PrivateRoute>
          ) : (
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          )
        }
      />
      <Route
        path="/assignments/:assignmentId"
        element={
          roles && roles.find((role) => role === "REVIEWER") ? (
            <PrivateRoute>
              <CodeReviewerAssignmentView />
            </PrivateRoute>
          ) : (
            <PrivateRoute>
              <AssignmentView />
            </PrivateRoute>
          )
        }
      />
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Homepage />} />
      <Route path="/registration" element={<Register />} />
    </Routes>
  );
}

export default App;
