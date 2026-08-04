import asyncio, base64, json, os, sys, time, urllib.request
import websockets

CDP = "http://127.0.0.1:9222"
OUT = os.environ.get("DRIVE_OUT", ".")


def page_ws():
    for _ in range(40):
        try:
            tabs = json.load(urllib.request.urlopen(f"{CDP}/json"))
            for t in tabs:
                if t.get("type") == "page" and t.get("webSocketDebuggerUrl"):
                    return t["webSocketDebuggerUrl"]
        except Exception:
            pass
        time.sleep(0.5)
    raise SystemExit("no debuggable page")


class Session:
    def __init__(self, ws):
        self.ws = ws
        self.n = 0

    async def send(self, method, **params):
        self.n += 1
        await self.ws.send(json.dumps({"id": self.n, "method": method, "params": params}))
        while True:
            msg = json.loads(await self.ws.recv())
            if msg.get("id") == self.n:
                return msg.get("result", {})

    async def click(self, x, y):
        for kind in ("mousePressed", "mouseReleased"):
            await self.send("Input.dispatchMouseEvent", type=kind, x=x, y=y,
                            button="left", clickCount=1, buttons=1 if kind == "mousePressed" else 0)
            await asyncio.sleep(0.05)
        await asyncio.sleep(0.4)

    async def type(self, text):
        for ch in text:
            await self.send("Input.dispatchKeyEvent", type="keyDown", text=ch, unmodifiedText=ch)
            await self.send("Input.dispatchKeyEvent", type="keyUp", text=ch, unmodifiedText=ch)
            await asyncio.sleep(0.02)
        await asyncio.sleep(0.3)

    async def shot(self, name):
        r = await self.send("Page.captureScreenshot", format="png")
        path = f"{OUT}/{name}.png"
        open(path, "wb").write(base64.b64decode(r["data"]))
        print("shot:", name)


async def main(steps):
    url = page_ws()
    async with websockets.connect(url, max_size=None) as ws:
        s = Session(ws)
        await s.send("Page.enable")
        await s.send("Runtime.enable")
        for step in steps:
            kind, *args = step.split(":", 2)
            if kind == "click":
                await s.click(float(args[0]), float(args[1]))
            elif kind == "type":
                await s.type(args[0])
            elif kind == "shot":
                await s.shot(args[0])
            elif kind == "wait":
                await asyncio.sleep(float(args[0]))
            elif kind == "eval":
                r = await s.send("Runtime.evaluate", expression=args[0], returnByValue=True)
                print("eval:", json.dumps(r.get("result", {}).get("value"))[:400])


asyncio.run(main(sys.argv[1:]))
