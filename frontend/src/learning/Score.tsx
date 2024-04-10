import React from "react";
import { Button } from "@mui/joy";

export const Score = (): JSX.Element => {
  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full">
      <div className="bg-[#9bd1e5] overflow-hidden w-[360px] h-[800px] relative">
        <div className="absolute w-[563px] h-[668px] top-[156px] left-[-182px]">
          <div className="absolute w-[500px] h-[500px] top-0 left-0 bg-[#d1faff2b] rounded-[250px]" />
          <div className="absolute w-[170px] h-[170px] top-[498px] left-[393px] bg-[#a4d8e9] rounded-[85px]" />
          <Button className="!flex !absolute !left-[190px] !bg-[#007aff] !w-[336px] !top-[569px]">
            {" "}
            "홈으로"
          </Button>
          <Button className="!flex !absolute !left-[190px] !bg-[#007aff] !w-[336px] !top-[498px]">
            "해설 보기"
          </Button>
          <div className="absolute w-[340px] h-[340px] top-[31px] left-[192px] rounded-[170px] border-[20px] border-solid border-[#55a2fd]" />
          <div className="absolute w-[350px] h-[390px] top-[6px] left-[190px]">
            <div className="relative w-[312px] h-[308px] top-[7px] left-[14px]">
              <div className="absolute w-[262px] h-[206px] top-[80px] left-[19px] overflow-hidden">
                <div className="relative w-[293px] h-[200px] left-[38px]">
                  <div className="absolute w-[200px] h-[200px] top-0 left-0 bg-white rounded-[100px]" />
                  <div className="absolute w-[187px] top-[113px] left-[6px] rotate-[0.48deg] [font-family:'Abhaya_Libre_Medium-Regular',Helvetica] font-normal text-[#55a2fd] text-[50px] text-center tracking-[0] leading-[normal]">
                    90.34
                  </div>
                  <div className="left-[31px] absolute w-[262px] top-[78px] [font-family:'Alfa_Slab_One-Regular',Helvetica] font-normal text-[#1e1e1e] text-[20px] tracking-[1.20px] leading-[normal] whitespace-nowrap">
                    grainy days
                  </div>
                </div>
              </div>
              <div className="absolute w-[262px] h-[206px] top-[80px] left-[19px] overflow-hidden">
                <div className="relative w-[325px] h-[200px] left-[38px]">
                  <div className="absolute w-[200px] h-[200px] top-0 left-0 bg-white rounded-[100px]" />
                  <div className="absolute w-[187px] top-[113px] left-[6px] rotate-[0.48deg] [font-family:'Abhaya_Libre_Medium-Regular',Helvetica] font-normal text-[#55a2fd] text-[50px] text-center tracking-[0] leading-[normal]">
                    80
                  </div>
                  <div className="left-[63px] absolute w-[262px] top-[78px] [font-family:'Alfa_Slab_One-Regular',Helvetica] font-normal text-[#1e1e1e] text-[20px] tracking-[1.20px] leading-[normal] whitespace-nowrap">
                    Score
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        {/*<div className="absolute w-[139px] h-[139px] top-[-20px] left-[-22px] bg-[#a4d8e9] rounded-[69.5px]">*/}
        {/*    <DirectionLeftDarkModeFalse className="!absolute !w-[24px] !h-[24px] !top-[57px] !left-[52px]" />*/}
        {/*</div>*/}
      </div>
    </div>
  );
};
