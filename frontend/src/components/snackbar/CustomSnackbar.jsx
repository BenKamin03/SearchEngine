import * as React from "react";
import { Transition } from "react-transition-group";
import { IoClose } from "react-icons/io5";
import Snackbar from "@mui/material/Snackbar";
import { IconButton } from "@mui/material";

export default function CustomSnackbar({
    open,
    onClose,
    autoHideDuration = 5000,
    children,
    sx = {},
    selectedTheme,
}) {
    const nodeRef = React.useRef(null);

    const defaultStyle = {
        transition: `transform 400ms ease-in-out`,
        transform: "translateX(100%)",
    };

    const transitionStyles = {
        entered: { transform: "translateX(0)" },
        exited: { transform: "translateX(100%)" },
    };

    const transitionSnackBar = (state) => (
        <div
            ref={nodeRef}
            style={{
                ...defaultStyle,
                ...transitionStyles[state],
                ...sx,
            }}
            className={`flex items-center justify-between p-4 ${selectedTheme == 'light' ? 'bg-white text-black' : 'bg-neutral-900 text-white'} rounded-lg shadow-lg space-x-2`}
        >
            {children}
            <IconButton onClick={onClose} sx={{color: selectedTheme == 'light' ? 'black' : 'white'}}>
                <IoClose />
            </IconButton>
        </div>
    );

    const timerRef = React.useRef(null);

    const [status, setStatus] = React.useState(open ? "entered" : "exited");

    React.useEffect(() => {
        if (open) {
            setStatus("entered");
        } else {
            setStatus("exited");
        }
    }, [open]);

    return (
        <Snackbar
            autoHideDuration={autoHideDuration}
            anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            open={open}
            onClose={onClose}
            style={{
                position: "fixed",
                zIndex: 5500,
                bottom: "16px",
                right: "16px",
                maxWidth: "560px",
                minWidth: "300px",
            }}
        >
            {transitionSnackBar(status)}
        </Snackbar>
    );
}
