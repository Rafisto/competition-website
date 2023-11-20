import { Box, Divider } from "@mui/material";
import ContestCard, {
    ContestCardProps,
} from "../../components/contests/contest_card";
import { Event } from "@mui/icons-material";
import IconTypography from "../../css/icon_typography";

const contest_list = [
    {
        id: 0,
        title: "RW-E01-2023",
        description: "Electronics Contest",
        tags: ["electronics", "circuit design"],
    },
    {
        id: 1,
        title: "RW-P01-2024",
        description: "Upcoming Programming Contest",
        tags: ["programming"],
    },
];

const Contests = () => {
    return (
        <>
            <Box sx={{ padding: "3px" }}>
                <IconTypography>
                    <Event />
                    <>&nbsp; Current and upcoming competitions</>
                </IconTypography>
                <Divider sx={{ marginBlock: "6px" }} />
                {contest_list.map((contest: ContestCardProps) => {
                    return (
                        <ContestCard
                            key={`contest-id-${contest.id}`}
                            {...contest}
                        />
                    );
                })}
            </Box>
        </>
    );
};

export default Contests;
