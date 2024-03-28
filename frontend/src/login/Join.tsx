import React from "react";
import BgCircle from "../components/BgCircle";
import { IoIosArrowRoundBack } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";

interface Input {
  username: string;
  user_id: string;
  password: string;
  confirmPassword: string; //비밀번호 확인용 전송 x
}

function Join() {
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
    setValue,
  } = useForm<Input>();
  const goBack = () => {
    navigate("/");
  };
  const onSubmit = async (data: Input) => {
    console.log(data.password, data.confirmPassword);
    if (data.password !== data.confirmPassword) {
      setError("confirmPassword", {
        type: "manual",
        message: "비밀번호가 일치하지 않습니다.",
      });
    } else {
      console.log(data);
      const { username, user_id, password } = data;
      setValue("username", "");
      setValue("user_id", "");
      setValue("password", "");
      setValue("confirmPassword", "");
      //const res = await axiosApi.post("/join");
      //console.log(res.data);
    }
  };

  return (
    <div className="bg-[#9bd1e5] flex flex-row justify-center w-full h-screen">
      <div className="bg-[#9bd1e5] overflow-hidden w-[450px] h-screen relative flex flex-col px-8">
        <BgCircle />
        <div onClick={goBack} className="z-10 mt-8">
          <IoIosArrowRoundBack className="w-12 h-12 text-black hover:text-gray-300" />
        </div>

        <span className="z-10 text-[36px] text-[#007AFF] font-extrabold mt-28">
          회원가입
        </span>

        <form
          onSubmit={handleSubmit(onSubmit)}
          className="z-10 flex flex-col justify-center align-middle mt-28"
        >
          <input
            {...register("username", { required: true })}
            placeholder="이름"
            className="mb-3 rounded-[30px] h-[45px] shadow-[0px_4px_4px_#00000040] px-4"
          />
          {errors.username && (
            <span className="text-center text-[red] font-bold">
              이름을 입력해주세요
            </span>
          )}
          <input
            {...register("user_id", { required: true })}
            placeholder="아이디"
            className="my-3 rounded-[30px] h-[45px] shadow-[0px_4px_4px_#00000040] px-4"
          />
          {errors.user_id && (
            <span className="text-center text-[red] font-bold">
              아이디를 입력해주세요
            </span>
          )}

          <input
            {...register("password", { required: true })}
            placeholder="비밀번호"
            type="password"
            className="my-3 rounded-[30px] h-[45px] shadow-[0px_4px_4px_#00000040] px-4"
          />
          {errors.password && (
            <span className="text-center text-[red] font-bold">
              비밀번호를 입력해주세요
            </span>
          )}

          <input
            {...register("confirmPassword", { required: true })}
            placeholder="비밀번호 확인"
            type="password"
            className="my-3 rounded-[30px] h-[45px] shadow-[0px_4px_4px_#00000040] px-4"
          />

          {errors.confirmPassword && (
            <span className="text-center text-[red] font-bold">
              {errors.confirmPassword.type === "manual"
                ? "비밀번호가 일치하지 않습니다"
                : "비밀번호를 한번더 입력해 주세요"}
            </span>
          )}

          <button className="mt-6 rounded-[30px] bg-[#495867] h-[45px] text-[white] hover:bg-[gray] hover:text-[#495867]">
            회원가입
          </button>
        </form>
      </div>
    </div>
  );
}

export default Join;
