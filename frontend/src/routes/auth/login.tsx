import Header from "../../components/header";
import "./login.css";
import { LoginStructure } from "./login_structure";
import { KeycloakForm } from "./forms/keycloak_form";
import LoginForm from "./forms/login_form";
import { Divider } from "@mui/material";
import { BackgroundStyle, Background } from "../../css/background";

const LoginDivider = () => {
    return <Divider sx={{ margin: "40px" }} />;
};

const Login = () => {
    return (
        <>
            <Header />
            <div style={BackgroundStyle}>
                <div style={Background}></div>
                <LoginStructure>
                    <>
                        <LoginDivider />
                        <KeycloakForm />
                        <LoginDivider />
                        <LoginForm disabled />
                    </>
                </LoginStructure>
            </div>
        </>
    );
};

export default Login;
