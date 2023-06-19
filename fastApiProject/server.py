import io
import os
import uuid

from fastapi import FastAPI
from fastapi import UploadFile, File
from PIL import Image

app = FastAPI()

@app.post("/predict")
async def predict(file: UploadFile = File(...)):

    image = await imageOpen(file)

    new_uuid = uuid.uuid4()
    #파일 이름이 겹치면 안되므로 uuid를 생성해서 저장함
    image_name, image_path = await getImageNameAndPath(new_uuid, file.filename)
    image.save(image_path)
    # 모델에 이미지 추론 수행
    await runPredict(image_path)
    storedFilePath = find_files_by_name(image_name)
    results = read_label_file(storedFilePath)
    result = getResults(results)
    return result

#predict test
async def runPredict(image_path):
    yolo_command = "yolo task=detect mode=predict model='./model/finalModel.pt' show=True conf=0.1 source='{}' save_txt=True".format(image_path)
    os.system(yolo_command)


def getResults(results):
    result = []
    if (results is None):
        return {"result" : "-1"}
    else:
        for line in results.split("\n"):
            index = line.find(" ")
            if(index != -1):
                tmpResult = line[:index]
                result.append(tmpResult)
        return {"result": result}
async def getImageNameAndPath(new_uuid, filename):
    extension = filename.split(".")[-1].lower()  # 파일 확장자 추출 및 소문자로 변환
    image_name = str(new_uuid) + "." + extension
    image_path = './images/' + image_name
    return image_name, image_path


def read_label_file(root_directory):
    labels_directory = os.path.join(root_directory, 'labels')

    if not os.path.exists(labels_directory) or not os.path.isdir(labels_directory):
        return

    for file_name in os.listdir(labels_directory):
        file_path = os.path.join(labels_directory, file_name)
        if os.path.isfile(file_path) and file_name.endswith('.txt'):
            with open(file_path, 'r') as file:
                contents = file.read()
                return contents

async def imageOpen(file):
    image = Image.open(io.BytesIO(await file.read()))
    return image

def find_files_by_name(target_name):
    for root, dirs, files in os.walk('./runs/detect'):
        for file in files:
            if file == target_name:
                file_path = os.path.join(root, file)
                directoryPath = os.path.dirname(file_path)
                return directoryPath
