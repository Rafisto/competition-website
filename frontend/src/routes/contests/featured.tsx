import { Box, Divider } from "@mui/material";
import { EmojiEvents } from "@mui/icons-material";
import IconTypography from "../../css/icon_typography";
const Featured = () => {
    return (
        <>
            <Box sx={{ padding: "3px" }}>
                <IconTypography>
                    <EmojiEvents />
                    <>&nbsp; Featured</>
                </IconTypography>
                <Divider sx={{ marginBlock: "6px" }} />
                Featured event
            </Box>
        </>
    );
};

export default Featured;
