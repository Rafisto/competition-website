import { Key } from "@mui/icons-material";
import { Button, Grid, TextField } from "@mui/material";
import { CSSProperties } from "react";
import { Link } from "react-router-dom";

const LoginBarStyle: CSSProperties = {
    background: "#101010",
    height: "100%",
    textAlign: "center",
};

const InputStyle: CSSProperties = {
    width: "90%",
    margin: "8px",
};

const PlainLinkStyle: CSSProperties = {
    color: "#ffffff",
}

const LoginForm = () => {
    return (
        <div>
            <Grid container sx={{ height: "100vh" }}>
                <Grid item xs={2} />
                <Grid item xs={3} sx={LoginBarStyle}>
                    <Key
                        sx={{
                            marginTop: "30%",
                            fontSize: "30pt",
                            marginBottom: "8px",
                        }}
                    />
                    <div>
                        <TextField
                            id="username"
                            label="username"
                            variant="outlined"
                            sx={InputStyle}
                        />
                        <TextField
                            id="password"
                            label="password"
                            type="password"
                            variant="outlined"
                            sx={InputStyle}
                        />
                        <Button
                            variant="contained"
                            sx={{ ...InputStyle, padding: "10px" }}
                        >
                            Login
                        </Button>
                        <div
                            style={{
                                textAlign: "right",
                                width: "90%",
                                margin: "auto",
                            }}
                        >
                            <Link to="/login/reset" style={PlainLinkStyle}>I forgot my password</Link>
                        </div>
                    </div>
                </Grid>
            </Grid>
        </div>
    );
};

export default LoginForm;
