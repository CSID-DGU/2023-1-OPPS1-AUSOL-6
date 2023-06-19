import io
import os
import uuid

from fastapi import FastAPI
from fastapi import UploadFile, File
from PIL import Image

app = FastAPI()


@app.get("/")
async def hello():
    return {"message": "Hello, JSON!"}


@app.post("/predict2")
async def predict(file: bytes = File(...)):
    # 요청 데이터 출력
    try:
        file_str = file.decode('utf-8')
    except UnicodeDecodeError:
        file_str = file.decode('latin-1')
    print(f"Request Data:\n{file_str}")
    image = Image.open(io.BytesIO(file))
    print(image.show())
    # 파싱 및 처리 로직 작성
    # ...

    return {"message": "Request processed successfully"}