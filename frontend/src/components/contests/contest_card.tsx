import {
    Box,
    Button,
    Card,
    CardActions,
    CardContent,
    Chip,
    Typography,
} from "@mui/material";

type ContestCardProps = {
    id: number;
    title: string;
    description: string;
    tags: string[];
};

const ContestCard = ({ id, title, description, tags }: ContestCardProps) => {
    return (
        <Card variant="outlined" sx={{ marginBottom: "6px" }}>
            <CardContent>
                <Box>
                    <Typography>
                        <span style={{ fontSize: "16pt" }}>{title}</span>
                        <span style={{ fontSize: "10pt", float: "right" }}>
                            {id}
                        </span>
                    </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary">
                    {description}
                </Typography>
            </CardContent>
            <CardActions>
                {tags.map((tag) => (
                    <Chip key={`chip-${tag}`} label={tag} variant="outlined" />
                ))}
                <Button size="small" style={{ marginLeft: "auto" }}>
                    Join
                </Button>
            </CardActions>
        </Card>
    );
};

export type { ContestCardProps };
export default ContestCard;
