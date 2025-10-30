import "bootstrap/dist/css/bootstrap.min.css"
import React, { useState } from "react"
import { Container, Row, Col, Card, Form, Button, Alert } from "react-bootstrap"
import { useNavigate } from "react-router-dom"

export default function LoginPage() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [msg, setMsg] = useState(null)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMsg(null)
    try {
      const res = await fetch("http://localhost:3001/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      })
      const data = await res.json().catch(() => ({}))
      if (!res.ok) {
        setMsg({ variant: "danger", text: data?.message || "Login fallito" })
        return
      }
      if (data.token) {
        localStorage.setItem("token", data.token)
        setMsg({ variant: "success", text: "Login effettuato" })
        setTimeout(() => navigate("/"), 700)
      } else {
        setMsg({ variant: "warning", text: "Nessun token ricevuto" })
      }
    } catch {
      setMsg({ variant: "danger", text: "Errore di rete" })
    }
  }

  return (
    <Container
      fluid
      className="d-flex align-items-center"
      style={{ paddingTop: 56 }}
    >
      <Row className="w-100 justify-content-center">
        <Col xs={11} sm={10} md={8} lg={5} xl={4}>
          <Card className="shadow-lg rounded border">
            <Card.Body className="p-4">
              <h4 className="mb-3">Login</h4>
              {msg && <Alert variant={msg.variant}>{msg.text}</Alert>}
              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="loginEmail">
                  <Form.Label>Email</Form.Label>
                  <Form.Control
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="name@example.com"
                    required
                  />
                </Form.Group>
                <Form.Group className="mb-3" controlId="loginPassword">
                  <Form.Label>Password</Form.Label>
                  <Form.Control
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </Form.Group>
                <div className="d-flex justify-content-start">
                  <Button type="submit" variant="primary">
                    Login
                  </Button>
                </div>
              </Form>

              <div className="text-center mt-3">
                <small className="text-muted">
                  Non hai un account?{" "}
                  <span
                    role="button"
                    onClick={() => navigate("/register")}
                    style={{ cursor: "pointer", color: "#0d6efd" }}
                  >
                    Registrati
                  </span>
                </small>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}
