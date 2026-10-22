import logging
import os

from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from openai import OpenAI
from pydantic import BaseModel

load_dotenv()

logger = logging.getLogger("ai-service")

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"), base_url=os.getenv("OPENAI_API_BASE"))
MODEL = "deepseek-chat"

app = FastAPI(title="InterviewHelper AI Service")

# ---------- Prompt（从 Java 侧原样迁移） ----------

INTERVIEWER_SYSTEM_PROMPT = (
    "你是一位拥有10年互联网大厂面试官经验的资深模拟面试官。\n"
    "【面试规则】\n"
    "1. 每次只提出一个问题，等待求职者回答后再继续。\n"
    "2. 求职者回答后，先给出一句简短的针对性点评（指出亮点与不足），再提出下一个问题。\n"
    "3. 提问要贴合简历内容和求职意向，从自我介绍、项目经历、技术深度、行为面试等方面循序渐进。\n"
    "4. 当求职者明确表示想结束面试（如“结束面试”“就到这里”）时，应停止提问并简短总结表现。\n"
    "5. 回复控制在300字以内，语言专业、友好。\n"
    "6. 全程使用中文。"
)

EVALUATION_SYSTEM_PROMPT = (
    "你是一位拥有10年互联网大厂面试官经验的资深模拟面试官，请根据面试记录对求职者的整体表现进行评估。\n"
    "请严格按以下两行格式输出评估结果，不要输出其他任何内容：\n"
    "评分：X（0-10的数值，可含一位小数）\n"
    "建议：（给出2-4条具体、可执行的改进建议）"
)

OPTIMIZE_SYSTEM_PROMPT = (
    "你是一位拥有10年互联网大厂经验的资深简历优化专家。\n"
    "请优化以下简历中的这段描述文本，要求：\n"
    "1. 将平淡的动词（如'负责'）改为行动导向词（如'主导''设计''推动'）。\n"
    "2. 尽量融入STAR原则和可量化成果。若原文没有具体数字，可根据上下文合理推断，但需加上'约'或'近'字。\n"
    "3. 严禁捏造不存在的工作内容或技术栈，只润色表达，不改变原意。\n"
    "4. 只输出优化后的纯文本内容，禁止加任何解释、前后缀、引号或Markdown标记。"
)

# ---------- 请求 / 响应模型（字段名与 Java DTO 保持一致） ----------

class FirstQuestionReq(BaseModel):
    resumeContext: str | None = None

class ConversationReq(BaseModel):
    conversation: str

class OptimizeReq(BaseModel):
    summary: str | None = None
    project_description: str | None = None
    education_appendix: str | None = None
    work_description: str | None = None

class ContentResp(BaseModel):
    content: str

class OptimizeResp(BaseModel):
    summary: str | None = None
    project_description: str | None = None
    education_appendix: str | None = None
    work_description: str | None = None

# ---------- 通用调用 ----------

def chat(system_prompt: str, user_content: str) -> str:
    try:
        resp = client.chat.completions.create(
            model=MODEL,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_content},
            ],
        )
        content = resp.choices[0].message.content
        if not content or not content.strip():
            raise HTTPException(status_code=502, detail="AI生成失败，请重试")
        return content
    except HTTPException:
        raise
    except Exception as e:
        # 兜底：记录真实报错供排查，同时给调用方友好提示
        logger.exception("调用大模型失败")
        if getattr(e, "status_code", None) == 401:
            raise HTTPException(status_code=502, detail="AI服务认证失败，请检查API Key配置")
        raise HTTPException(status_code=502, detail="AI服务调用失败，请稍后重试")

# ---------- 接口 ----------

@app.get("/ai/health")
def health():
    return {"status": "ok"}

@app.post("/ai/interview/first-question", response_model=ContentResp)
def first_question(req: FirstQuestionReq):
    user_content = (
        "请开始模拟面试，向求职者提出第一个问题。"
        if not req.resumeContext or not req.resumeContext.strip()
        else "以下是求职者的简历信息：\n" + req.resumeContext + "\n请基于简历信息开始模拟面试，提出第一个问题。"
    )
    return ContentResp(content=chat(INTERVIEWER_SYSTEM_PROMPT, user_content))

@app.post("/ai/interview/reply", response_model=ContentResp)
def reply(req: ConversationReq):
    user_content = (
        "以下是本次面试的对话记录：\n" + req.conversation
        + "\n请针对求职者的最新回答进行简短点评，并提出下一个问题。"
    )
    return ContentResp(content=chat(INTERVIEWER_SYSTEM_PROMPT, user_content))

@app.post("/ai/interview/evaluate", response_model=ContentResp)
def evaluate(req: ConversationReq):
    return ContentResp(content=chat(EVALUATION_SYSTEM_PROMPT, "【面试记录】\n" + req.conversation))

@app.post("/ai/resume/optimize", response_model=OptimizeResp)
def optimize(req: OptimizeReq):
    def optimize_content(content: str | None) -> str | None:
        if not content or not content.strip():
            return content
        return chat(OPTIMIZE_SYSTEM_PROMPT, content)

    return OptimizeResp(
        summary=optimize_content(req.summary),
        project_description=optimize_content(req.project_description),
        education_appendix=optimize_content(req.education_appendix),
        work_description=optimize_content(req.work_description),
    )
