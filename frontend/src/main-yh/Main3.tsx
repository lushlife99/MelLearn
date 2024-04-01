import React, {useEffect, useState} from "react";
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import {Avatar, CardActionArea} from '@mui/material';
import axios from "axios";

export const Main3 = (): JSX.Element => {
    const [artist, setArtist] = useState([]);

    // TODO 지워야함 테스트용
    const options = {
        method: 'GET',
        url: 'https://spotify-scraper.p.rapidapi.com/v1/chart/artists/top',
        headers: {
            'X-RapidAPI-Key': 'SIGN-UP-FOR-KEY',
            'X-RapidAPI-Host': 'spotify-scraper.p.rapidapi.com'
        }
    };
    const getData = async () => {
        try {
            const response = await axios.request(options);
            console.log(response.data)
            console.log(response.data.artists[0].name)
            // setTrakcs(response.data.tracks);
            setArtist(response.data.artists)
            return response.data;

        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        // 맨 처음 이 홈페이지 실행되면 인기가수 리스트 불러와야함 .
        const getTracks =  getData();

    }, [])

    return (
        <div className="container mx-auto px-4 ">
            <div className="bg-black  m-auto h-24 flex justify-center items-center  ">
                <h1 className="text-3xl font-semibold mb-4 mt-8 text-center text-white">인기 가수 리스트 </h1>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 bg-black">
                {artist.map((artist: any) => (
                    // <Card key={artist.id}>
                    //     <Card.Img variant="top" src={artist.visuals.avatar[0].url} />
                    //     <Card.Body>
                    //         <Card.Title>{artist.name}</Card.Title>
                    //     </Card.Body>
                    // </Card>
                    <Card key={artist.id} sx={{maxWidth: 345, backgroundColor: "black"}}>
                        <CardActionArea color="black">
                            {/*<CardMedia*/}
                            {/*    component="img"*/}
                            {/*    height="140"*/}
                            {/*    image={artist.visuals.avatar[0].url}*/}
                            {/*    alt=""*/}
                            {/*/>*/}
                            <CardContent>
                                <Typography bgcolor="black">
                                    <Avatar
                                        alt=""
                                        src={artist.visuals.avatar[0].url}
                                        sx={{width: 180, height: 180}}
                                    />
                                </Typography>
                                <Typography gutterBottom variant="h5" borderColor="black" color="white" component="div">
                                    <br/>
                                    {artist.name}
                                </Typography>
                            </CardContent>
                        </CardActionArea>
                    </Card>

                ))}
                <div className="container mx-auto px-4 ">
                    <div className="bg-black  m-auto h-24 flex justify-center items-center  ">
                        <h1 className="text-3xl font-semibold mb-4 mt-8 text-center text-white">인기 가수 리스트 </h1>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 bg-black">
                        {artist.map((artist: any) => (
                            // <Card key={artist.id}>
                            //     <Card.Img variant="top" src={artist.visuals.avatar[0].url} />
                            //     <Card.Body>
                            //         <Card.Title>{artist.name}</Card.Title>
                            //     </Card.Body>
                            // </Card>
                            <Card key={artist.id} sx={{maxWidth: 345, backgroundColor: "black"}}>
                                <CardActionArea color="black">
                                    {/*<CardMedia*/}
                                    {/*    component="img"*/}
                                    {/*    height="140"*/}
                                    {/*    image={artist.visuals.avatar[0].url}*/}
                                    {/*    alt=""*/}
                                    {/*/>*/}
                                    <CardContent>
                                        <Typography bgcolor="black">
                                            <Avatar
                                                alt=""
                                                src={artist.visuals.avatar[0].url}
                                                sx={{width: 180, height: 180}}
                                            />
                                        </Typography>
                                        <Typography gutterBottom variant="h5" borderColor="black" color="white"
                                                    component="div">
                                            <br/>
                                            {artist.name}
                                        </Typography>
                                    </CardContent>
                                </CardActionArea>
                            </Card>

                        ))}

                    </div>
                </div>

            </div>
        </div>

    );
};
