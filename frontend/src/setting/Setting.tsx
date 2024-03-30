import React from "react";

import {Dropdown, DropdownButton, ProgressBar} from 'react-bootstrap';


import {SpotifyLogo} from "./SpotifyLogo";


export const Setting = (): JSX.Element => {

    const clientId = 'f7d3088794d14901af7c8bf354326039';


    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');

    if(code){
        const accessToken =  getToken(code);
        console.log(accessToken);

    }
    async function getToken(code : string){
        let codeVerifier = localStorage.getItem('code_verifier');
        console.log("get Token: " + codeVerifier)

        let params = new URLSearchParams();
        params.set('client_id', clientId);
        params.set('grant_type', 'authorization_code');
        params.set('code', code);
        params.set('redirect_uri', 'http://localhost:3000/callback');
        // @ts-ignore
        params.set('code_verifier', codeVerifier);

        const payload = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params,
        }

        const body = await fetch("https://accounts.spotify.com/api/token", payload)
        return await body.json();

    }

    const now = 50;


    return (
        <div className="bg-white flex flex-row justify-center w-full">
            <div className="bg-white overflow-hidden w-[360px] h-[800px] relative">
                <div className="absolute w-[64px] top-[202px] left-[148px] [font-family:'Inter-Bold',Helvetica] font-bold text-[#000000cc] text-[17px] tracking-[0] leading-[normal]">
                    hyomin
                </div>
                <div className="w-[98px] top-[456px] left-[-9px] text-[#a39c9c] text-[15px] text-center whitespace-nowrap absolute [font-family:'Inter-Regular',Helvetica] font-normal tracking-[0] leading-[normal]">
                    환경설정
                </div>
                <div className="w-[112px] top-[501px] left-[18px] text-[#000000cc] text-[17px] absolute [font-family:'Inter-Regular',Helvetica] font-normal tracking-[0] leading-[normal]">
                    학습 언어
                </div>
                <div className="w-[112px] top-[540px] left-[18px] text-[#000000cc] text-[17px] absolute [font-family:'Inter-Regular',Helvetica] font-normal tracking-[0] leading-[normal]">
                    난이도
                </div>
                {/*Drop down */}
                <div className="absolute w-[50px] h-[15px] top-[505px] left-[265px]">
                    <Dropdown>
                    <Dropdown.Toggle variant="success" id="dropdown-basic">
                        Language
                    </Dropdown.Toggle>

                    <Dropdown.Menu>
                        <Dropdown.Item href="#/action-1">English</Dropdown.Item>
                        <Dropdown.Item href="#/action-2">Japan</Dropdown.Item>
                    </Dropdown.Menu>
                    </Dropdown>
                </div>
                <div className="absolute w-[49px] h-[15px] top-[541px] left-[299px]">

                    <DropdownButton id="dropdown-basic-button" title="Level">
                        <Dropdown.Item href="#/action-1">1</Dropdown.Item>
                        <Dropdown.Item href="#/action-2">2</Dropdown.Item>
                        <Dropdown.Item href="#/action-3">3</Dropdown.Item>
                    </DropdownButton>
                </div>
                {/*환경설정 밑에 줄 */}
                <hr className="absolute w-[360px] h-[2px] top-[482px] left-0 border-2 object-cover" />
                <div className="absolute w-[108px] h-[33px] top-[701px] left-[126px]">
                    <img src="/spotify/Spotify.png" alt=""/>
                    <SpotifyLogo/>


                </div>

                <div
                    className="absolute w-[83px] top-[196px] left-[37px] [font-family:'Inter-SemiBold',Helvetica] font-semibold text-black text-[30px] tracking-[-1.36px] leading-[99.4px] whitespace-nowrap">
                    Lv. 2
                </div>
                <div className="absolute w-[680px] h-[76px] top-[273px] left-[30px]">
                    <p className="absolute w-[673px] top-0 left-[7px] [font-family:'Inter-SemiBold',Helvetica] font-semibold text-[#0000004c] text-[20px] tracking-[0.09px] leading-[normal]">
                        800 points to next level
                    </p>
                    {/*<img className="absolute w-[300px] h-[20px] top-[44px] left-0" alt="Group" src="group-289345.png" />*/}
                    <div className="absolute w-[300px] h-[20px] top-[44px] left-0">

                        <ProgressBar animated now={45}  label={`${now}%`} />
                    </div>
                    <div className="absolute w-[20px] h-[20px] top-[43px] left-[274px]">
                    </div>
                </div>

                <div className="absolute w-[30px] h-[30px] top-[40px] left-[20px]">
                    <img src="/icon/back.png" alt="" />
                </div>

                <div className="absolute w-[90px] h-[90px] top-[89px] left-[135px] object-contain ">
                {/*    여기 레벨 아이콘 자리  자리  3개 만들어야함 */}
                    <img src="/diamond/Diamond_1.png" alt="Dia" />
                </div>
            </div>
        </div>
    );
};

export default Setting;
