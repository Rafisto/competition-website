import { Grid } from "@mui/material";
import Contests from "../contests/contests";
import Leaderboard from "../contests/leaderboard";
import Featured from "../contests/featured";

const HomeLayout = () => {
    return (
        <div style={{ margin: "20px" }}>
            <Grid container>
                <Grid item xs={3}>
                    <Contests />
                </Grid>
                <Grid item xs={7}>
                    <Featured />
                </Grid>
                <Grid item xs={2}>
                    <Leaderboard />
                </Grid>
            </Grid>
        </div>
    );
};

export default HomeLayout;
