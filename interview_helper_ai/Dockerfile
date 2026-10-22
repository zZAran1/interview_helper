FROM python:3.12-slim
WORKDIR /app

# 先只拷贝依赖清单，利用镜像层缓存加速重建
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY main.py .

EXPOSE 8000
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000","--workers","4"]