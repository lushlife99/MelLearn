import React, {useEffect, useState} from "react";
import axios from "axios";
import Card from "@mui/material/Card";
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import { CardActionArea } from '@mui/material';


export const Main2 = (): JSX.Element => {
    const [tracks, setTrakcs] = useState([]);

    // TODO 지워야함 테스트용
    const options = {
        method: "GET",
        url: "https://spotify-scraper.p.rapidapi.com/v1/chart/tracks/top?region=US",
        headers: {
            "X-RapidAPI-Key": "SIGN-UP-FOR-KEY",
            "X-RapidAPI-Host": "spotify-scraper.p.rapidapi.com",
        },
    };

    const getChartdata = async () => {
        try {
            const response = await axios.request(options);
            console.log(response.data.tracks);
            setTrakcs(response.data.tracks);

            return response.data;
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        // 맨 처음 이 홈페이지 실행되면 인기가수 리스트 불러와야함 .
        const getTracks =  getChartdata();


    }, [])

    return (
        <div className="container mx-auto px-4 ">
            <div className="bg-black  m-auto h-24 flex justify-center items-center  ">
            <h1 className="text-3xl font-semibold mb-4 mt-8 text-center text-white">사용자 추천 음악/인기 음악 </h1>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 bg-black">
                {/*{tracks.map((track: any) => (*/}
                {/*    // <Card key={track.id}>*/}
                {/*    //     <Card.Img variant="top" src={track.album.cover[0].url} />*/}
                {/*    //     <Card.Body>*/}
                {/*    //         <Card.Title>{track.name}<br/></Card.Title>*/}
                {/*    //         <Card.Text>{track.artists[0].name}</Card.Text>*/}
                {/*    //     </Card.Body>*/}
                {/*    // </Card>*/}
                {/*))}*/}
                {tracks.map((track: any) => (

                <Card sx={{ maxWidth: 250, backgroundColor: "black" }}>
                    <CardActionArea>
                        <CardMedia
                            component="img"
                            height="140"
                            image={track.album.cover[0].url}
                            alt=""
                        />
                        <CardContent>
                            <Typography gutterBottom variant="h5" color="white" component="div">
                                {track.name}
                            </Typography>
                            <Typography variant="body2" color="white">
                                {track.artists[0].name}
                            </Typography>
                        </CardContent>
                    </CardActionArea>
                </Card>
                ))}

                    </div>
        </div>

    );
};
