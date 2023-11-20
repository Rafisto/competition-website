import { Key } from "@mui/icons-material";
import { Grid } from "@mui/material";
import { CSSProperties } from "react";

type LoginStructureForm = {
    children: JSX.Element;
};

const LoginBarStyle: CSSProperties = {
    background: "#101010",
    height: "100%",
    textAlign: "center",
};

const KeyTopStyle: CSSProperties = {
    marginTop: "50%",
    fontSize: "30pt",
    marginBottom: "8px",
}

export const LoginStructure = ({ children }: LoginStructureForm) => {
    return (
        <div>
            <Grid container sx={{ height: "100vh" }}>
                <Grid item xs={2} />
                <Grid item xs={3} sx={LoginBarStyle}>
                    <Key sx={KeyTopStyle}/>
                    {children}
                </Grid>
            </Grid>
        </div>
    );
};
