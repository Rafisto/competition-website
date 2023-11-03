import Header from "../../components/header";
import LoginForm from "./login_form";
import "./login.css";
import { CSSProperties } from "react";

const BackgroundStyle: CSSProperties = {
    position: "absolute",
    top: "0",
    zIndex: "-1",
    height: "100%",
    width: "100%",
};

const Background: CSSProperties = {
    position: "absolute",
    top: "0",
    backgroundImage: "url(wallpaper.jpg)",
    backgroundSize: "cover",
    backgroundPosition: "bottom",
    filter: "blur(5px)",
    height: "100%",
    width: "100%",
    zIndex: "-2",
};

const Login = () => {
    return (
        <>
            <Header />
            <div style={BackgroundStyle}>
                <div style={Background}></div>
                <LoginForm />
            </div>
        </>
    );
};

export default Login;
