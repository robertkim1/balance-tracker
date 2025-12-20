"use client"

import { useEffect, useState } from "react"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select"
import { Transaction, TransactionType, PayPeriod } from "@/types/transaction"

interface Props {
  open: boolean
  onOpenChange: (open: boolean) => void
  initialData: Transaction | null
  onSave: (tx: Transaction) => void
}

export default function TransactionModal({
  open,
  onOpenChange,
  initialData,
  onSave
}: Props) {
  const [sourceName, setSourceName] = useState("")
  const [amount, setAmount] = useState("")
  const [date, setDate] = useState("")
  const [type, setType] = useState<TransactionType>(TransactionType.INCOME);
  const [payPeriod, setPayPeriod] = useState<PayPeriod>(PayPeriod.WEEKLY);

  useEffect(() => {
    if (initialData) {
      setSourceName(initialData.sourceName)
      setAmount(String(initialData.amount))
      setDate(initialData.date)
      setType(initialData.type)
      setPayPeriod(initialData.payPeriod)
    } else {
      setSourceName("")
      setAmount("")
      setDate("")
      setType(TransactionType.INCOME)
      setPayPeriod(PayPeriod.WEEKLY)
    }
  }, [initialData, open])

  function submit() {
    onSave({
      id: initialData?.id ?? crypto.randomUUID(),
      sourceName,
      amount: Number(amount),
      date,
      type,
      payPeriod
    })
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            {initialData ? "Edit Transaction" : "Add Transaction"}
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-3">
          <Input
            placeholder="Source name"
            value={sourceName}
            onChange={e => setSourceName(e.target.value)}
          />

          <Input
            type="number"
            placeholder="Amount"
            value={amount}
            onChange={e => setAmount(e.target.value)}
          />

          <Input
            type="date"
            value={date}
            onChange={e => setDate(e.target.value)}
          />

          <Select value={type} onValueChange={v => setType(v as TransactionType)}>
            <SelectTrigger>
              <SelectValue placeholder="Type" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="INCOME">Income</SelectItem>
              <SelectItem value="DEBT">Debt</SelectItem>
            </SelectContent>
          </Select>

          <Select
            value={payPeriod}
            onValueChange={v => setPayPeriod(v as PayPeriod)}
          >
            <SelectTrigger>
              <SelectValue placeholder="Pay period" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="WEEKLY">Weekly</SelectItem>
              <SelectItem value="BIWEEKLY">Biweekly</SelectItem>
              <SelectItem value="MONTHLY">Monthly</SelectItem>
            </SelectContent>
          </Select>

          <Button className="w-full" onClick={submit}>
            Save
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}
