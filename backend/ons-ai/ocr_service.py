import io
from functools import lru_cache

import easyocr
import numpy as np
from PIL import Image

@lru_cache(maxsize=1)
def get_reader():
    return easyocr.Reader(["ar", "en"], gpu=False)

def read_text(image_bytes: bytes) -> dict:
    try:
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        image_array = np.array(image)

        results = get_reader().readtext(
            image_array,
            detail=0,
            paragraph=True
        )

        extracted_text = "\n".join(
            str(item).strip()
            for item in results
            if str(item).strip()
        )

        if not extracted_text:
            return {
                "success": False,
                "text": "",
                "message": "No readable text was found in the image."
            }

        return {
            "success": True,
            "text": extracted_text,
            "message": "Text extracted successfully."
        }

    except Exception as exc:
        return {
            "success": False,
            "text": "",
            "message": f"OCR processing failed: {type(exc).__name__}"
        }
