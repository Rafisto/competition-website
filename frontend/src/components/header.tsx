import { AppBar, Toolbar } from "@mui/material";
import { AccountCircle, EmojiEvents, Info, Search } from "@mui/icons-material";
import "./header.css";

const Header = () => {
    return (
        <AppBar position="static" color="primary" className="transparent-style">
            <Toolbar>
                <div className="logo-style link-prop rounded">
                    <EmojiEvents />
                    <span>&nbsp;ContestHub</span>
                </div>
                <div className="separator" />
                <div className="logo-group rounded-group">
                    <div className="logo-style link-prop">
                        <Search />
                        <span>&nbsp;Browse</span>
                    </div>
                    <div className="logo-style link-prop">
                        <Info />
                        <span>&nbsp;About</span>
                    </div>
                    <div className="logo-style link-prop">
                        <AccountCircle />
                        <span>&nbsp;Login</span>
                    </div>
                </div>
            </Toolbar>
        </AppBar>
    );
};

export default Header;
