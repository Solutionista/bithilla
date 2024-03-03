import { Subscription } from "@hilla/frontend";
import { Button } from "@hilla/react-components/Button.js";
import { TextField } from "@hilla/react-components/TextField.js";
import { getClockCancellable } from "Frontend/generated/ReactiveEndpoint";
import { useState } from "react";

export default function ReactiveView() {
  const [serverTime, setServerTime] = useState("");
  const [subscription, setSubscription] = useState<Subscription<string> | undefined>();

  const toggleServerClock = async () => {
    if (subscription) {
      subscription.cancel();
      setSubscription(undefined);
    } else {
      setSubscription(getClockCancellable().onNext((time) => {
        setServerTime(time);
      }));
    }
  }

  return (
      <section className="flex p-m gap-m items-end">
        <TextField label="Server time" value={serverTime} readonly />
        <Button onClick={toggleServerClock}>Toggle server clock</Button>
      </section>
  );
}