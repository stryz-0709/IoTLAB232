# IoTLAB232

### Open virutal port
```
socat -d -d pty,raw,echo=1 pty,raw,echo=1
```

### Receive data
```
cat < /dev/ttysXXX
```

### Send data
```
echo \!1:T:32# > /dev/ttysXXX
```