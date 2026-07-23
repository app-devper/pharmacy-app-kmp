import os

import uvicorn


def main() -> None:
    uvicorn.run(
        "pharmacy_mock_api.app:app",
        host=os.getenv("MOCK_API_HOST", "0.0.0.0"),
        port=int(os.getenv("MOCK_API_PORT", "8787")),
        reload=False,
    )


if __name__ == "__main__":
    main()
