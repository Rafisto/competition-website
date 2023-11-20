import { Typography } from "@mui/material";
import { CSSProperties } from "react";

const IconTypographyStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    flexWrap: "wrap",
};

type IconTypographyProps = {
    children: JSX.Element | JSX.Element[];
};

const IconTypography = ({ children }: IconTypographyProps) => {
    return <Typography style={IconTypographyStyle}>{children}</Typography>;
};

export default IconTypography;
