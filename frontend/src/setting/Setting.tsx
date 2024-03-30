import React, { useEffect, useState } from "react";
import { ProgressBar, Nav, NavDropdown } from "react-bootstrap";
import axiosApi from "../api";
interface Member {
  id: number;
  langType: string;
  level: string;
  levelPoint: number;
  memberId: string;
  name: string;
}

export const Setting = (): JSX.Element => {
  // const clientId = 'f7d3088794d14901af7c8bf354326039';

  // const urlParams = new URLSearchParams(window.location.search);
  // const code = urlParams.get('code');

  // if(code){
  //     const accessToken =  getToken(code);
  //     console.log(accessToken);

  // }
  // async function getToken(code : string){
  //     let codeVerifier = localStorage.getItem('code_verifier');
  //     console.log("get Token: " + codeVerifier)

  //     let params = new URLSearchParams();
  //     params.set('client_id', clientId);
  //     params.set('grant_type', 'authorization_code');
  //     params.set('code', code);
  //     params.set('redirect_uri', 'http://localhost:3000/callback');
  //     // @ts-ignore
  //     params.set('code_verifier', codeVerifier);

  //     const payload = {
  //         method: 'POST',
  //         headers: {
  //             'Content-Type': 'application/x-www-form-urlencoded',
  //         },
  //         body: params,
  //     }

  //     const body = await fetch("https://accounts.spotify.com/api/token", payload)
  //     return await body.json();

  // }
  const [member, setMember] = useState<Member>(); //멤버 정보 데이터
  const [languages, setLanguages] = useState<string[]>(); //언어 정보
  const [langauge, setLanguage] = useState<string>("en"); // 지원하는 언어 정보 (default en)

  // 멤버 정보 조회
  const getMember = async () => {
    const res = await axiosApi.get("/api/member/info");
    if (res.status === 200) {
      setMember(res.data);
    }
  };

  // 지원 언어 조회
  const getLanguage = async () => {
    const res = await axiosApi.get("/api/support/language");
    if (res.status === 200) {
      setLanguages(res.data);
    }
  };

  //언어 선택 후 변경된 정보 전송
  const selectLanguage = async (eventKey: any, event: Object) => {
    const res = await axiosApi.put("/api/member/info", {
      langType: eventKey,
    });
    if (res.status === 200) {
      setMember(res.data);
      setLanguage(eventKey);
    }
  };
  useEffect(() => {
    getMember();
    getLanguage();
  }, []);

  const now = 50;

  return (
    <div className="flex flex-row justify-center w-full bg-white">
      <div className="bg-white overflow-hidden w-[360px] h-[800px] relative">
        <div className="absolute w-[64px] top-[202px] left-[148px] [font-family:'Inter-Bold',Helvetica] font-bold text-[#000000cc] text-[17px] tracking-[0] leading-[normal]">
          {member?.name}
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
          {/* 언어 설정 drop down */}
          <Nav>
            <NavDropdown title={langauge} onSelect={selectLanguage}>
              {languages &&
                languages.map((lang: string, index: number) => (
                  <NavDropdown.Item key={index} eventKey={lang}>
                    {lang}
                  </NavDropdown.Item>
                ))}
            </NavDropdown>
          </Nav>
        </div>
        <div className="absolute w-[49px] h-[15px] top-[541px] left-[299px]">
          <Nav>
            <NavDropdown title="Lv.1">
              <NavDropdown.Item>Action</NavDropdown.Item>
            </NavDropdown>
          </Nav>
        </div>
        {/*환경설정 밑에 줄 */}
        <hr className="absolute w-[360px] h-[2px] top-[482px] left-0 border-2 object-cover" />
        <div className="absolute w-[108px] h-[33px] top-[701px] left-[126px]">
          <img src="/spotify/Spotify.png" alt="" />
          {/* <SpotifyLogo /> */}
        </div>

        <div className="absolute w-[83px] top-[196px] left-[37px] [font-family:'Inter-SemiBold',Helvetica] font-semibold text-black text-[30px] tracking-[-1.36px] leading-[99.4px] whitespace-nowrap">
          {member?.level}
        </div>
        {/** member point -> Beginner -> 1000 - levelPoint Intermediate -> 2000 - levelPoint Advanced -> 3000 - levelPoint  */}
        <div className="absolute w-[680px] h-[76px] top-[273px] left-[30px]">
          <p className="absolute w-[673px] top-0 left-[7px] [font-family:'Inter-SemiBold',Helvetica] font-semibold text-[#0000004c] text-[20px] tracking-[0.09px] leading-[normal]">
            {1000 - (member?.levelPoint || 0)} points to next level
          </p>
          {/*<img className="absolute w-[300px] h-[20px] top-[44px] left-0" alt="Group" src="group-289345.png" />*/}
          <div className="absolute w-[300px] h-[20px] top-[44px] left-0">
            <ProgressBar animated now={45} label={`${now}%`} />
          </div>
          <div className="absolute w-[20px] h-[20px] top-[43px] left-[274px]"></div>
        </div>

        <div className="absolute w-[30px] h-[30px] top-[40px] left-[20px]">
          <img src="/icon/back.png" alt="" />
        </div>

        <div className="absolute w-[90px] h-[90px] top-[89px] left-[135px] object-contain">
          {/*    여기 레벨 아이콘 자리  자리  3개 만들어야함 */}
          <img src="/diamond/Diamond_1.png" alt="Dia" />
        </div>
      </div>
    </div>
  );
};

export default Setting;
