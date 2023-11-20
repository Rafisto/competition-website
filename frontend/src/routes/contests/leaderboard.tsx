import {
    TableContainer,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Card,
    Box,
    Divider,
} from "@mui/material";
import IconTypography from "../../css/icon_typography";
import { LeaderboardOutlined } from "@mui/icons-material";

const leaderboardData = [
    { user: "User1", score: 1000 },
    { user: "User2", score: 950 },
    { user: "User3", score: 850 },
];

const Leaderboard = () => {
    return (
        <Box sx={{ margin: "3px" }}>
            <IconTypography>
                <LeaderboardOutlined />
                <>&nbsp; All time leaderboard</>
            </IconTypography>
            <Divider sx={{ marginBlock: "6px" }} />
            <Card variant="outlined">
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>User</TableCell>
                                <TableCell align="right">Score</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {leaderboardData.map((row, index) => (
                                <TableRow key={index}>
                                    <TableCell>{row.user}</TableCell>
                                    <TableCell align="right">
                                        {row.score}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Card>
        </Box>
    );
};

export default Leaderboard;
