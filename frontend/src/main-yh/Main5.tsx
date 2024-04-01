import React, {useEffect, useState} from 'react';
import {Divider, IconButton, InputBase, Paper} from "@mui/material";
import SearchIcon from '@mui/icons-material/Search';

export const Main5 = () => {
    const [search, setSearch] = useState('');

    // TODO 작업중

    const options = {
        method: 'GET',
        url: 'https://spotify-scraper.p.rapidapi.com/v1/search',
        params: {term: 'Jazz'},
        headers: {
            'X-RapidAPI-Key': 'SIGN-UP-FOR-KEY',
            'X-RapidAPI-Host': 'spotify-scraper.p.rapidapi.com'
        }
    };



    const handleSubmit = async (event : any) => {
        if(event.key === 'Enter') {
            event.preventDefault(); // 기본 동작 중지
            console.log("enter")

            // Search 요청 을 보낸다.



        }

        // 여기에서 검색 기능을 구현하거나 다른 작업을 수행할 수 있습니다.
        console.log(search)

    };

    useEffect(() => {

    }, [])

    return(
        <>
            <div style={{backgroundColor : 'black'}}>
            <div style={{display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: '80px'}}>
            <Paper
                component="form"
                onSubmit={handleSubmit}
                sx={{ p: '2px 4px', display: 'flex', alignItems: 'center', width: 400 , backgroundColor: 'gray'}}
            >

                <IconButton sx={{ p: '10px' }} aria-label="search" >
                    <SearchIcon />
                </IconButton>
                <Divider sx={{ height: 28, m: 0.5 }} orientation="vertical" />

                <input style={{backgroundColor : 'gray',  flex: 1 , color: 'white'}}
                       type="text"
                       placeholder="Search"
                       value={search}
                       onChange={(event) => setSearch(event.target.value)}
                       onKeyDown={handleSubmit}
                />

            </Paper>
            </div>


            </div>

        </>
    );
};