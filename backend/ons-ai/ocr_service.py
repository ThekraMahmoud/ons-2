import io
import os
import subprocess
from functools import lru_cache

import numpy as np
from PIL import Image


@lru_cache(maxsize=1)
def get_reader():
    # Keep the original engine available locally without loading it on small hosts.
    import easyocr
    return easyocr.Reader(["ar", "en"], gpu=False, verbose=False)


def read_text(image_bytes: bytes) -> dict:
    try:
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        engine = os.environ.get("OCR_ENGINE", "easyocr").strip().lower()
        if engine == "tesseract":
            image.thumbnail((1600, 1600))
            payload = io.BytesIO()
            image.save(payload, format="PNG")
            result = subprocess.run(
                ["tesseract", "stdin", "stdout", "-l", "ara+eng", "--psm", "3"],
                input=payload.getvalue(), capture_output=True, timeout=60, check=True,
            )
            extracted_text = result.stdout.decode("utf-8").strip()
        elif engine == "easyocr":
            results = get_reader().readtext(np.array(image), detail=0, paragraph=True)
            extracted_text = "\n".join(str(item).strip() for item in results if str(item).strip())
        else:
            raise ValueError("Unsupported OCR engine")

        if not extracted_text:
            return {"success": False, "text": "", "message": "No readable text was found in the image."}
        return {"success": True, "text": extracted_text, "message": "Text extracted successfully."}
    except Exception as exc:
        return {"success": False, "text": "", "message": f"OCR processing failed: {type(exc).__name__}"}
