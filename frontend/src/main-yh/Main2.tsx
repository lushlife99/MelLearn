import React, {useEffect, useState} from "react";
import axios from "axios";
import Card from "@mui/material/Card";
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import { CardActionArea } from '@mui/material';
import {useSelector} from "react-redux";
import {RootState} from "../redux/store";


export const Main2 = (): JSX.Element => {

    const { chartData, chartLoading, chartError } = useSelector(
        (state: RootState) => state.chart
    );

    return (
        <div className="container mx-auto px-4 ">
            <div className="bg-black  m-auto h-24 flex justify-center items-center  ">
            <h1 className="text-3xl font-semibold mb-4 mt-8 text-center text-white">인기 음악 </h1>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 bg-black">
                {chartData.tracks.map((track: any) => (

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
