import { useEffect, useState } from "react";
import "./App.css";
import { Route, Routes } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import Dashboard from "./Dashboard";
import AdminDashboard from "./AdminDashboard";
import GuestDashboard from "./GuestDashboard";
import Homepage from "./Homepage";
import Login from "./Login";
import PrivateRoute from "./PrivateRoute";
import AdminPanelRequests from "./AdminPanelRequests"
import AdminPanelUsers from "./AdminPanelUsers"
import AdminPanelReports from "./AdminPanelReports"
import "bootstrap/dist/css/bootstrap.min.css";
import { useUser } from "./UserProvider";
import Register from "./Register";
import CreateReport from "./CreateReport";
import ChangeReport from "./ChangeReport";

function App() {
  const [roles, setRoles] = useState([]);
  const user = useUser();

  useEffect(() => {
    const getRolesFromJwt = () => {
      if (user.jwt) {
        const decodeJwt = jwtDecode(user.jwt);
        return decodeJwt.authorities;
      }
    };
  
    setRoles(getRolesFromJwt());
  }, [user.jwt]);
  

  return (
    <Routes>
      <Route
        path="/dashboard"
        element={
          roles && roles.find((role) => role === "ADMIN") ? (
            <PrivateRoute>
              <AdminDashboard />
            </PrivateRoute>
          ) : roles && roles.find((role) => role === "GUEST") ? (
            <PrivateRoute>
              <GuestDashboard />
            </PrivateRoute>
          ) : (
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          )
        }
      />
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Homepage />} />
      <Route path="/registration" element={<Register />} />
      <Route path="/adminPanelRequests" element={<PrivateRoute><AdminPanelRequests/></PrivateRoute>} />
      <Route path="/adminPanelUsers" element={<PrivateRoute><AdminPanelUsers/></PrivateRoute>} />
      <Route path="/adminPanelReports" element={<PrivateRoute><AdminPanelReports/></PrivateRoute>} />
      <Route path="/createReport" element={<PrivateRoute><CreateReport/></PrivateRoute>} />
      <Route path="/changeReport/:id" element={<PrivateRoute><ChangeReport/></PrivateRoute>} />
    </Routes>
  );
  
}

export default App;