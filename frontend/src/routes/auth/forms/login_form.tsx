import { Button, TextField } from "@mui/material";
import { CSSProperties } from "react";
import { Link } from "react-router-dom";

const InputStyle: CSSProperties = {
    width: "90%",
    margin: "8px",
};

const PlainLinkStyle: CSSProperties = {
    color: "#ffffff",
};

type LoginFormProps = {
    disabled: boolean;
};

const LoginForm = ({ disabled }: LoginFormProps) => {
    return (
        <>
            <div>
                <TextField
                    id="username"
                    label={!disabled ? "username" : ""}
                    variant="outlined"
                    sx={InputStyle}
                    disabled={disabled}
                />
                <TextField
                    id="password"
                    label={!disabled ? "password" : ""}
                    type="password"
                    variant="outlined"
                    sx={InputStyle}
                    disabled={disabled}
                />
                <Button
                    variant="contained"
                    sx={{ ...InputStyle, padding: "10px" }}
                    disabled={disabled}
                >
                    {!disabled ? "Login" : "Local auth unavailable"}
                </Button>
                <div
                    style={{
                        textAlign: "right",
                        width: "90%",
                        margin: "auto",
                    }}
                >
                    {!disabled && (
                        <Link to="/login/reset" style={PlainLinkStyle}>
                            I forgot my password
                        </Link>
                    )}
                </div>
            </div>
        </>
    );
};

export default LoginForm;
