import React, {useEffect} from 'react';
import {useLocation, useNavigate, useParams} from "react-router-dom";

function Callback() {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const error = queryParams.get('error')
    const code = queryParams.get('code')

    // TODO 지우기 
    console.log("error: " +queryParams.get('error') )
    console.log("code: " +queryParams.get('code') )

    const nav =  useNavigate();

    useEffect(() => {
        if(error === 'access_denied'){
            console.log("useEffect callback")
            nav("/");
            return;
        }
        nav("/home");

    }, [])

    return (
        <></>
    );
}

export default Callback;