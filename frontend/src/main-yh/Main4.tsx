import React, {useEffect, useState} from 'react';
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import CardMedia from "@mui/material/CardMedia";
import axios from "axios";



export const Main4 = (): JSX.Element => {
    //     7255ad630cmshdf9fda3f9dea3b0p1e9379jsn615bba3b42d1

    const [albums, setAlbums] = useState([]);
    const [artistName, setArtistName] = useState('');
    const [artistImg, setArtisImg] = useState('');


    //TODO 지울것 테스트용
    const artistId = {
        method: 'GET',
        url: 'https://spotify-scraper.p.rapidapi.com/v1/artist/search',
        params: {name: 'IU'},
        headers: {
            'X-RapidAPI-Key': '7255ad630cmshdf9fda3f9dea3b0p1e9379jsn615bba3b42d1',
            'X-RapidAPI-Host': 'spotify-scraper.p.rapidapi.com'
        }
    };

    const getData = async () => {
        try {
            const response = await axios.request(artistId);
            // console.log(response.data)
            // console.log(response.data.name)
            // console.log(response.data.visuals.avatar[0].url)
            setArtistName(response.data.name)
            setArtisImg(response.data.visuals.avatar[0].url)

            const id = response.data.id

            const listArtistAlbums = {
                method: 'GET',
                url: 'https://spotify-scraper.p.rapidapi.com/v1/artist/albums',
                params: {
                    artistId: id
                },
                headers: {
                    'X-RapidAPI-Key': '7255ad630cmshdf9fda3f9dea3b0p1e9379jsn615bba3b42d1',
                    'X-RapidAPI-Host': 'spotify-scraper.p.rapidapi.com'
                }
            };

            const response2 = await axios.request(listArtistAlbums);
            console.log(response2.data)
            setAlbums(response2.data.albums.items)

            return response2.data;
            // return response.data;
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        // 아티스트가 작업한 곡들 가져오기
          const data =  getData();
    }, [])

    return (
        <>
            <div style={{backgroundColor : 'black'}}>
            <div style={{display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: '80px'}}>
                <Card sx={{maxWidth: 500, maxHeight: 500, backgroundColor: "black", marginBottom: '5px'}}>
                    <CardMedia
                        sx={{height: 300, width: 350, borderRadius: '14px'}}
                        image={artistImg}
                        title=""
                    />
                    <CardContent sx={{textAlign: 'center'}}>
                        <Typography gutterBottom
                                    variant="h5"
                                    color="white"
                                    borderColor="black"
                                    component="div">
                            {artistName}
                        </Typography>
                    </CardContent>
                </Card>
            </div>

            <div className="container mx-auto px-2 flex flex-col items-center justify-center h-screen">

                {albums.map((album: any, index : number) => (

                    <Card key = {index} sx={{maxWidth: 500, backgroundColor: "black", display: 'flex' , marginBottom: '20px'}}>
                        <CardMedia
                            sx={{height: 120, width: 120, flex: '1 1 auto', borderRadius: '8px'}} // 이미지가 왼쪽에 오도록 flex 속성 추가
                            image={album.cover[0].url}
                            title=""
                        />
                        <CardContent
                            sx={{width: 400, textAlign: 'left', flex: '1 1 auto'}}> {/* 텍스트가 오른쪽에 오도록 flex 속성 추가 */}
                            <Typography gutterBottom
                                        variant="h5"
                                        color="white"
                                        borderColor="black"
                                        component="div">
                                {album.name}
                            </Typography>
                            <Typography gutterBottom
                                        variant="h6"
                                        color="white"
                                        borderColor="black"
                                        component="div">
                                {artistName}
                            </Typography>
                        </CardContent>
                    </Card>
                ))}
            </div>
            </div>
        </>
    );
};

