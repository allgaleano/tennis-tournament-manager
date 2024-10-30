import React from 'react'
import { IconType } from 'react-icons/lib'

interface SectionHeaderInterface {
  title: string
  Icon: IconType
}

const SectionHeader = ({
  title,
  Icon
} : SectionHeaderInterface) => {
  return (
    <h1 className="font-semibold text-2xl flex items-center gap-2">
      <Icon />
      {title}
    </h1>
  )
}

export default SectionHeader