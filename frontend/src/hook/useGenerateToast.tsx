import toast from "react-hot-toast";
import {useCallback} from "react";

export default function useGenerateToast() {
    const TOAST_TIMEOUT = 2000;
    const successToast = useCallback((message: string) => {
        const toastId = `successToast_${message.replace(/\W/g, '')}`;
        toast.success(`${message} 🤗`, {id: toastId});
        setTimeout(() => toast.dismiss(toastId), TOAST_TIMEOUT);
    }, []);
    const errorToast = useCallback((message: string, err: any) => {
        const toastId = `errorToast_${message.replace(/\W/g, '')}`;
        const errorMsg = err.response?.data.error || err.response?.data.message || err.message || "";
        toast.error(`${message} 😱\n${errorMsg}`, {id: toastId});
        setTimeout(() => toast.dismiss(toastId), TOAST_TIMEOUT);
    }, []);

    return {successToast, errorToast};
}
