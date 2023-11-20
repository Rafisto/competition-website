import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { AppTheme } from "./css/app_theme";
import React from "react";
import ReactDOM from "react-dom/client";
import Home from "./routes/home/home";
import Login from "./routes/auth/login";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Home />,
    },
    {
        path: "/login",
        element: <Login />,
    },
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <ThemeProvider theme={AppTheme}>
            <CssBaseline />
            <RouterProvider router={router}></RouterProvider>
        </ThemeProvider>
    </React.StrictMode>
);
