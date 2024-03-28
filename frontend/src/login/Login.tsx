import React, { useState } from "react";
import { useForm } from "react-hook-form";
import axiosApi from "../api";

interface Input {
  id: string;
  password: string;
}
function Login() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Input>();
  const onSubmit = async (data: Input) => {
    console.log(data);
    //const res = await axiosApi.post("/login");
    //console.log(res.data);
  };
  console.log(errors);
  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col px-8">
        <div className="absolute w-[521px] h-[667px] top-[157px] left-[-140px]">
          <div className="absolute w-[500px] h-[500px] top-0 left-0 bg-[#d1faff2b] rounded-[250px]" />
          <div className="absolute w-[170px] h-[170px] top-[497px] left-[351px] bg-[#a4d8e9] rounded-[85px]" />
        </div>
        <div className="absolute w-[216px] h-[162px] top-[-20px] left-[-22px]">
          <div className="absolute w-[139px] h-[139px] top-0 left-0 bg-[#a4d8e9] rounded-[69.5px]" />
        </div>

        <div className="z-10 mt-24">
          <div className="text-[#007aff] font-extrabold text-[40px] whitespace-normal">
            MelLearn
          </div>
          <p className="mt-12 font-bold text-[#a39c9c] text-[15px] whitespace-normal">
            신나는 노래와 함께 <br />
            언어를 공부해보세요
          </p>
        </div>

        <form
          onSubmit={handleSubmit(onSubmit)}
          className="z-10 flex flex-col justify-center align-middle mt-28"
        >
          <input
            {...register("id", { required: true })}
            placeholder="아이디"
            className="mb-3 rounded-[30px] h-[45px] shadow-[0px_4px_4px_#00000040] px-4"
          />
          {errors.id && (
            <span className="text-center text-[red] font-bold">
              아이디를 입력해주세요
            </span>
          )}
          <input
            {...register("password", { required: true })}
            type="password"
            placeholder="비밀번호"
            className="mt-3 mb-3 rounded-[30px] h-[45px] shadow-[0px_4px_4px_#00000040] px-4"
          />
          {errors.password && (
            <span className="text-center text-[red] font-bold">
              비밀번호를 입력해주세요
            </span>
          )}
          <button className="mt-6 rounded-[30px] bg-[#495867] h-[45px] text-[white] ">
            로그인
          </button>
        </form>

        <div className="z-10 flex flex-col mt-24 text-center ">
          <span className="text-[#232121] text-[15px] font-bold mb-4 ">
            아직 회원이 아니시라구요?
          </span>
          <div className="flex flex-wrap items-center justify-center ">
            <div className="h-[2px] bg-white w-32"></div>
            <span className="text-[#007AFF] font-bold mx-2">회원가입</span>
            <div className="h-[2px] bg-white w-32"></div>
          </div>
        </div>
      </div>
    </div>
  );
}
export default Login;
