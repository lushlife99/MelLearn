import React, { useEffect, useRef, useState } from "react";
import axiosApi from "../api";
import axios from "axios";

const Test = () => {
  // deviceID는 내가 실험해보려고 만든거 spotify sdk api 쓸때 사용 우리는 사용 X
  const [deviceID, setDeviceID] = useState("");
  // 이것도 우리가 스포티파이
  let spotifyPlayer: Spotify.Player | null = null;

  // 여기 token 은 Spotify 로그인할때 주는 토큰임 1
  const access =
    "BQBx-d9ntYl-9oJEjLslzh2jAnMGhtEKYX2vo9DtRlSI7qMueNRazwrA5nvfrdAIxYvy8axJJjWDfsW927wrP4oUE010gdUTZ__AQ0LWEr9OvA1F9vaw2pdqjKwL1b6cYU8iKXOoKVF5_d0AkfJart5pSMBcnRfTPC7G-DF08a7O6DS6vEUIaHmlAeYIAO4buJWfIk6EsLOJloEzaEkqAd3lq2bWMQDnC7M";

  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://sdk.scdn.co/spotify-player.js";
    script.async = true;
    document.body.appendChild(script);

    window.onSpotifyWebPlaybackSDKReady = () => {
      // 여기 token 은 Spotify 로그인할때 주는 토큰임 1
      const token = access;
      const newPlayer = new Spotify.Player({
        name: "MelLearn",
        getOAuthToken: (cb) => {
          cb(token);
        },
        volume: 0.5,
      });

      // Ready
      newPlayer.addListener("ready", ({ device_id }) => {
        console.log("device_id");
        console.log(device_id);
        setDeviceID(device_id);

        //transfer device
        // https://developer.spotify.com/documentation/web-api/reference/transfer-a-users-playback
        // 우리가만든 device에 제어권이 넘어짐
        console.log("axios put  ");
        const options = {
          method: "PUT",
          url: "https://api.spotify.com/v1/me/player",
          headers: {
            Authorization: "Bearer " + access,
            "Content-Type": "application/json",
          },
          data: {
            device_ids: [device_id],
            play: true,
          },
        };
        const response = axios.request(options);
        // transfer device end
      });

      // Not Ready
      newPlayer.addListener("not_ready", ({ device_id }) => {
        console.log("Device ID has gone offline", device_id);
      });

      newPlayer.addListener("initialization_error", ({ message }) => {
        console.error(message);
      });

      newPlayer.addListener("authentication_error", ({ message }) => {
        console.error(message);
      });

      newPlayer.addListener("account_error", ({ message }) => {
        console.error(message);
      });

      // 위에 만든 플레이어 29 line
      // newPlayer.connect() 만 사용하면 device만들어짐 이다음이  "ready" 가 실행됨  -> 38L
      newPlayer.connect().then((success) => {
        if (success) {
          spotifyPlayer = newPlayer;
        }
      });
    };
  }, []);

  const playToggle = () => {
    console.log("toggle play");
    // https://developer.spotify.com/documentation/web-api/reference/start-a-users-playback

    // https://api.spotify.com/v1/me/player/play
    const options1 = {
      method: "PUT",
      url: "https://api.spotify.com/v1/me/player/play",
      headers: {
        Authorization: "Bearer " + access,
        "Content-Type": "application/json",
      },
      data: {
        context_uri: "spotify:album:5ht7ItJgpBH7W6vJ5BqpPr",
        offset: {
          position: 5,
        },
        position_ms: 0,
      },
    };
    const response2 = axios.request(options1);
  };

  // @ts-ignore
  return (
    <>
      <button id="togglePlay" onClick={playToggle}>
        Toggle Play
      </button>
    </>
  );
};

export default Test;
