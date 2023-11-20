import { Link } from "@mui/icons-material";
import { Button } from "@mui/material";
import { CSSProperties } from "react";

const InputStyle: CSSProperties = {
    width: "90%",
    margin: "8px",
};

export const KeycloakForm = () => {
    return (
        <>
            <Button variant="contained" sx={{ ...InputStyle, padding: "10px" }}>
                <Link />
                Sign in through KeyCloak
            </Button>
        </>
    );
};
